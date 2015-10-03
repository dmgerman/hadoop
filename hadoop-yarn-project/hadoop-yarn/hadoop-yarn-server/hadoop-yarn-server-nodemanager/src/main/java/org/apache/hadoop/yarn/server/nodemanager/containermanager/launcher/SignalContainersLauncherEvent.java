begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher
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
name|launcher
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
name|SignalContainerCommand
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
comment|// This event can be triggered by one of the following flows
end_comment

begin_comment
comment|// WebUI -> Container
end_comment

begin_comment
comment|// CLI -> RM -> NM
end_comment

begin_class
DECL|class|SignalContainersLauncherEvent
specifier|public
class|class
name|SignalContainersLauncherEvent
extends|extends
name|ContainersLauncherEvent
block|{
DECL|field|command
specifier|private
specifier|final
name|SignalContainerCommand
name|command
decl_stmt|;
DECL|method|SignalContainersLauncherEvent (Container container, SignalContainerCommand command)
specifier|public
name|SignalContainersLauncherEvent
parameter_list|(
name|Container
name|container
parameter_list|,
name|SignalContainerCommand
name|command
parameter_list|)
block|{
name|super
argument_list|(
name|container
argument_list|,
name|ContainersLauncherEventType
operator|.
name|SIGNAL_CONTAINER
argument_list|)
expr_stmt|;
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
block|}
DECL|method|getCommand ()
specifier|public
name|SignalContainerCommand
name|getCommand
parameter_list|()
block|{
return|return
name|command
return|;
block|}
block|}
end_class

end_unit

