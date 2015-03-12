begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.metrics
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
name|metrics
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

begin_class
DECL|class|ContainerCreatedEvent
specifier|public
class|class
name|ContainerCreatedEvent
extends|extends
name|SystemMetricsEvent
block|{
DECL|field|containerId
specifier|private
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|allocatedResource
specifier|private
name|Resource
name|allocatedResource
decl_stmt|;
DECL|field|allocatedNode
specifier|private
name|NodeId
name|allocatedNode
decl_stmt|;
DECL|field|allocatedPriority
specifier|private
name|Priority
name|allocatedPriority
decl_stmt|;
DECL|field|nodeHttpAddress
specifier|private
name|String
name|nodeHttpAddress
decl_stmt|;
DECL|method|ContainerCreatedEvent ( ContainerId containerId, Resource allocatedResource, NodeId allocatedNode, Priority allocatedPriority, long createdTime, String nodeHttpAddress)
specifier|public
name|ContainerCreatedEvent
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|allocatedResource
parameter_list|,
name|NodeId
name|allocatedNode
parameter_list|,
name|Priority
name|allocatedPriority
parameter_list|,
name|long
name|createdTime
parameter_list|,
name|String
name|nodeHttpAddress
parameter_list|)
block|{
name|super
argument_list|(
name|SystemMetricsEventType
operator|.
name|CONTAINER_CREATED
argument_list|,
name|createdTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|allocatedResource
operator|=
name|allocatedResource
expr_stmt|;
name|this
operator|.
name|allocatedNode
operator|=
name|allocatedNode
expr_stmt|;
name|this
operator|.
name|allocatedPriority
operator|=
name|allocatedPriority
expr_stmt|;
name|this
operator|.
name|nodeHttpAddress
operator|=
name|nodeHttpAddress
expr_stmt|;
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
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
operator|.
name|hashCode
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
name|containerId
return|;
block|}
DECL|method|getAllocatedResource ()
specifier|public
name|Resource
name|getAllocatedResource
parameter_list|()
block|{
return|return
name|allocatedResource
return|;
block|}
DECL|method|getAllocatedNode ()
specifier|public
name|NodeId
name|getAllocatedNode
parameter_list|()
block|{
return|return
name|allocatedNode
return|;
block|}
DECL|method|getAllocatedPriority ()
specifier|public
name|Priority
name|getAllocatedPriority
parameter_list|()
block|{
return|return
name|allocatedPriority
return|;
block|}
DECL|method|getNodeHttpAddress ()
specifier|public
name|String
name|getNodeHttpAddress
parameter_list|()
block|{
return|return
name|nodeHttpAddress
return|;
block|}
block|}
end_class

end_unit

