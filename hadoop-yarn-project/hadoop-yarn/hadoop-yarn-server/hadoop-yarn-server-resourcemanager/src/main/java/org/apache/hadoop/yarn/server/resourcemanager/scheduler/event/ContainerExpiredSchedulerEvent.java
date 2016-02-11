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
name|server
operator|.
name|resourcemanager
operator|.
name|rmcontainer
operator|.
name|ContainerAllocationExpirer
import|;
end_import

begin_comment
comment|/**  * The {@link SchedulerEvent} which notifies that a {@link ContainerId}  * has expired, sent by {@link ContainerAllocationExpirer}   *  */
end_comment

begin_class
DECL|class|ContainerExpiredSchedulerEvent
specifier|public
class|class
name|ContainerExpiredSchedulerEvent
extends|extends
name|SchedulerEvent
block|{
DECL|field|containerId
specifier|private
specifier|final
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|increase
specifier|private
specifier|final
name|boolean
name|increase
decl_stmt|;
DECL|method|ContainerExpiredSchedulerEvent (ContainerId containerId)
specifier|public
name|ContainerExpiredSchedulerEvent
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|this
argument_list|(
name|containerId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|ContainerExpiredSchedulerEvent ( ContainerId containerId, boolean increase)
specifier|public
name|ContainerExpiredSchedulerEvent
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|boolean
name|increase
parameter_list|)
block|{
name|super
argument_list|(
name|SchedulerEventType
operator|.
name|CONTAINER_EXPIRED
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
name|increase
operator|=
name|increase
expr_stmt|;
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
DECL|method|isIncrease ()
specifier|public
name|boolean
name|isIncrease
parameter_list|()
block|{
return|return
name|increase
return|;
block|}
block|}
end_class

end_unit

