begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_class
DECL|class|ContainerStartMonitoringEvent
specifier|public
class|class
name|ContainerStartMonitoringEvent
extends|extends
name|ContainersMonitorEvent
block|{
DECL|field|vmemLimit
specifier|private
specifier|final
name|long
name|vmemLimit
decl_stmt|;
DECL|field|pmemLimit
specifier|private
specifier|final
name|long
name|pmemLimit
decl_stmt|;
DECL|field|cpuVcores
specifier|private
specifier|final
name|int
name|cpuVcores
decl_stmt|;
DECL|field|launchDuration
specifier|private
specifier|final
name|long
name|launchDuration
decl_stmt|;
DECL|field|localizationDuration
specifier|private
specifier|final
name|long
name|localizationDuration
decl_stmt|;
DECL|method|ContainerStartMonitoringEvent (ContainerId containerId, long vmemLimit, long pmemLimit, int cpuVcores, long launchDuration, long localizationDuration)
specifier|public
name|ContainerStartMonitoringEvent
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|long
name|vmemLimit
parameter_list|,
name|long
name|pmemLimit
parameter_list|,
name|int
name|cpuVcores
parameter_list|,
name|long
name|launchDuration
parameter_list|,
name|long
name|localizationDuration
parameter_list|)
block|{
name|super
argument_list|(
name|containerId
argument_list|,
name|ContainersMonitorEventType
operator|.
name|START_MONITORING_CONTAINER
argument_list|)
expr_stmt|;
name|this
operator|.
name|vmemLimit
operator|=
name|vmemLimit
expr_stmt|;
name|this
operator|.
name|pmemLimit
operator|=
name|pmemLimit
expr_stmt|;
name|this
operator|.
name|cpuVcores
operator|=
name|cpuVcores
expr_stmt|;
name|this
operator|.
name|launchDuration
operator|=
name|launchDuration
expr_stmt|;
name|this
operator|.
name|localizationDuration
operator|=
name|localizationDuration
expr_stmt|;
block|}
DECL|method|getVmemLimit ()
specifier|public
name|long
name|getVmemLimit
parameter_list|()
block|{
return|return
name|this
operator|.
name|vmemLimit
return|;
block|}
DECL|method|getPmemLimit ()
specifier|public
name|long
name|getPmemLimit
parameter_list|()
block|{
return|return
name|this
operator|.
name|pmemLimit
return|;
block|}
DECL|method|getCpuVcores ()
specifier|public
name|int
name|getCpuVcores
parameter_list|()
block|{
return|return
name|this
operator|.
name|cpuVcores
return|;
block|}
DECL|method|getLaunchDuration ()
specifier|public
name|long
name|getLaunchDuration
parameter_list|()
block|{
return|return
name|this
operator|.
name|launchDuration
return|;
block|}
DECL|method|getLocalizationDuration ()
specifier|public
name|long
name|getLocalizationDuration
parameter_list|()
block|{
return|return
name|this
operator|.
name|localizationDuration
return|;
block|}
block|}
end_class

end_unit

