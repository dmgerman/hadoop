begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.scheduler
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
name|nodemanager
operator|.
name|containermanager
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
name|event
operator|.
name|AbstractEvent
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
import|;
end_import

begin_comment
comment|/**  * Events consumed by the {@link ContainerScheduler}.  */
end_comment

begin_class
DECL|class|ContainerSchedulerEvent
specifier|public
class|class
name|ContainerSchedulerEvent
extends|extends
name|AbstractEvent
argument_list|<
name|ContainerSchedulerEventType
argument_list|>
block|{
DECL|field|container
specifier|private
specifier|final
name|Container
name|container
decl_stmt|;
comment|/**    * Create instance of Event.    * @param container Container.    * @param eventType EventType.    */
DECL|method|ContainerSchedulerEvent (Container container, ContainerSchedulerEventType eventType)
specifier|public
name|ContainerSchedulerEvent
parameter_list|(
name|Container
name|container
parameter_list|,
name|ContainerSchedulerEventType
name|eventType
parameter_list|)
block|{
name|super
argument_list|(
name|eventType
argument_list|)
expr_stmt|;
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
block|}
comment|/**    * Get the container associated with the event.    * @return Container.    */
DECL|method|getContainer ()
specifier|public
name|Container
name|getContainer
parameter_list|()
block|{
return|return
name|container
return|;
block|}
block|}
end_class

end_unit

