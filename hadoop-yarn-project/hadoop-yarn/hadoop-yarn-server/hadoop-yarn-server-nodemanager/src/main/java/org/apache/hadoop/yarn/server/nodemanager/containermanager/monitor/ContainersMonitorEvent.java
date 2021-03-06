begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor
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
name|monitor
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
name|event
operator|.
name|AbstractEvent
import|;
end_import

begin_class
DECL|class|ContainersMonitorEvent
specifier|public
class|class
name|ContainersMonitorEvent
extends|extends
name|AbstractEvent
argument_list|<
name|ContainersMonitorEventType
argument_list|>
block|{
DECL|field|containerId
specifier|private
specifier|final
name|ContainerId
name|containerId
decl_stmt|;
DECL|method|ContainersMonitorEvent (ContainerId containerId, ContainersMonitorEventType eventType)
specifier|public
name|ContainersMonitorEvent
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|ContainersMonitorEventType
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
name|containerId
operator|=
name|containerId
expr_stmt|;
block|}
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerId
return|;
block|}
block|}
end_class

end_unit

