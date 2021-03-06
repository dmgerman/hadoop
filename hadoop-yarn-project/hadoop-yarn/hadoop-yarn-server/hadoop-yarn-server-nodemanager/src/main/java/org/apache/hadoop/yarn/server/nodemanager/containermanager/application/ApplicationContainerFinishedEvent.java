begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.application
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
name|application
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
name|ContainerStatus
import|;
end_import

begin_class
DECL|class|ApplicationContainerFinishedEvent
specifier|public
class|class
name|ApplicationContainerFinishedEvent
extends|extends
name|ApplicationEvent
block|{
DECL|field|containerStatus
specifier|private
name|ContainerStatus
name|containerStatus
decl_stmt|;
comment|// Required by NMTimelinePublisher.
DECL|field|containerStartTime
specifier|private
name|long
name|containerStartTime
decl_stmt|;
DECL|method|ApplicationContainerFinishedEvent (ContainerStatus containerStatus, long containerStartTs)
specifier|public
name|ApplicationContainerFinishedEvent
parameter_list|(
name|ContainerStatus
name|containerStatus
parameter_list|,
name|long
name|containerStartTs
parameter_list|)
block|{
name|super
argument_list|(
name|containerStatus
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_CONTAINER_FINISHED
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerStatus
operator|=
name|containerStatus
expr_stmt|;
name|this
operator|.
name|containerStartTime
operator|=
name|containerStartTs
expr_stmt|;
block|}
DECL|method|getContainerID ()
specifier|public
name|ContainerId
name|getContainerID
parameter_list|()
block|{
return|return
name|containerStatus
operator|.
name|getContainerId
argument_list|()
return|;
block|}
DECL|method|getContainerStatus ()
specifier|public
name|ContainerStatus
name|getContainerStatus
parameter_list|()
block|{
return|return
name|containerStatus
return|;
block|}
DECL|method|getContainerStartTime ()
specifier|public
name|long
name|getContainerStartTime
parameter_list|()
block|{
return|return
name|containerStartTime
return|;
block|}
block|}
end_class

end_unit

