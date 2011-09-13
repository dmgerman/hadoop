begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.event
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
name|event
package|;
end_package

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
name|ApplicationId
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
name|ContainerStatus
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
name|rmnode
operator|.
name|RMNode
import|;
end_import

begin_class
DECL|class|NodeUpdateSchedulerEvent
specifier|public
class|class
name|NodeUpdateSchedulerEvent
extends|extends
name|SchedulerEvent
block|{
DECL|field|rmNode
specifier|private
specifier|final
name|RMNode
name|rmNode
decl_stmt|;
DECL|field|newlyLaunchedContainers
specifier|private
specifier|final
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|newlyLaunchedContainers
decl_stmt|;
DECL|field|completedContainersStatuses
specifier|private
specifier|final
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completedContainersStatuses
decl_stmt|;
DECL|method|NodeUpdateSchedulerEvent (RMNode rmNode, List<ContainerStatus> newlyLaunchedContainers, List<ContainerStatus> completedContainers)
specifier|public
name|NodeUpdateSchedulerEvent
parameter_list|(
name|RMNode
name|rmNode
parameter_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|newlyLaunchedContainers
parameter_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completedContainers
parameter_list|)
block|{
name|super
argument_list|(
name|SchedulerEventType
operator|.
name|NODE_UPDATE
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmNode
operator|=
name|rmNode
expr_stmt|;
name|this
operator|.
name|newlyLaunchedContainers
operator|=
name|newlyLaunchedContainers
expr_stmt|;
name|this
operator|.
name|completedContainersStatuses
operator|=
name|completedContainers
expr_stmt|;
block|}
DECL|method|getRMNode ()
specifier|public
name|RMNode
name|getRMNode
parameter_list|()
block|{
return|return
name|rmNode
return|;
block|}
DECL|method|getNewlyLaunchedContainers ()
specifier|public
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getNewlyLaunchedContainers
parameter_list|()
block|{
return|return
name|newlyLaunchedContainers
return|;
block|}
DECL|method|getCompletedContainers ()
specifier|public
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getCompletedContainers
parameter_list|()
block|{
return|return
name|completedContainersStatuses
return|;
block|}
block|}
end_class

end_unit

