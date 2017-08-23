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
name|server
operator|.
name|resourcemanager
operator|.
name|rmcontainer
operator|.
name|RMContainer
import|;
end_import

begin_comment
comment|/**  * Event used to release a container.  */
end_comment

begin_class
DECL|class|ReleaseContainerEvent
specifier|public
class|class
name|ReleaseContainerEvent
extends|extends
name|SchedulerEvent
block|{
DECL|field|container
specifier|private
specifier|final
name|RMContainer
name|container
decl_stmt|;
comment|/**    * Create Event.    * @param rmContainer RMContainer.    */
DECL|method|ReleaseContainerEvent (RMContainer rmContainer)
specifier|public
name|ReleaseContainerEvent
parameter_list|(
name|RMContainer
name|rmContainer
parameter_list|)
block|{
name|super
argument_list|(
name|SchedulerEventType
operator|.
name|RELEASE_CONTAINER
argument_list|)
expr_stmt|;
name|this
operator|.
name|container
operator|=
name|rmContainer
expr_stmt|;
block|}
comment|/**    * Get RMContainer.    * @return RMContainer.    */
DECL|method|getContainer ()
specifier|public
name|RMContainer
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

