begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|webapp
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlTransient
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
name|RMContext
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
name|ResourceManager
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
name|scheduler
operator|.
name|QueueMetrics
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
name|scheduler
operator|.
name|ResourceScheduler
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"userMetrics"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|UserMetricsInfo
specifier|public
class|class
name|UserMetricsInfo
block|{
DECL|field|appsSubmitted
specifier|protected
name|int
name|appsSubmitted
decl_stmt|;
DECL|field|appsCompleted
specifier|protected
name|int
name|appsCompleted
decl_stmt|;
DECL|field|appsPending
specifier|protected
name|int
name|appsPending
decl_stmt|;
DECL|field|appsRunning
specifier|protected
name|int
name|appsRunning
decl_stmt|;
DECL|field|appsFailed
specifier|protected
name|int
name|appsFailed
decl_stmt|;
DECL|field|appsKilled
specifier|protected
name|int
name|appsKilled
decl_stmt|;
DECL|field|runningContainers
specifier|protected
name|int
name|runningContainers
decl_stmt|;
DECL|field|pendingContainers
specifier|protected
name|int
name|pendingContainers
decl_stmt|;
DECL|field|reservedContainers
specifier|protected
name|int
name|reservedContainers
decl_stmt|;
DECL|field|reservedMB
specifier|protected
name|long
name|reservedMB
decl_stmt|;
DECL|field|pendingMB
specifier|protected
name|long
name|pendingMB
decl_stmt|;
DECL|field|allocatedMB
specifier|protected
name|long
name|allocatedMB
decl_stmt|;
DECL|field|reservedVirtualCores
specifier|protected
name|long
name|reservedVirtualCores
decl_stmt|;
DECL|field|pendingVirtualCores
specifier|protected
name|long
name|pendingVirtualCores
decl_stmt|;
DECL|field|allocatedVirtualCores
specifier|protected
name|long
name|allocatedVirtualCores
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|userMetricsAvailable
specifier|protected
name|boolean
name|userMetricsAvailable
decl_stmt|;
DECL|method|UserMetricsInfo ()
specifier|public
name|UserMetricsInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|UserMetricsInfo (final ResourceManager rm, final String user)
specifier|public
name|UserMetricsInfo
parameter_list|(
specifier|final
name|ResourceManager
name|rm
parameter_list|,
specifier|final
name|String
name|user
parameter_list|)
block|{
name|ResourceScheduler
name|rs
init|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|QueueMetrics
name|metrics
init|=
name|rs
operator|.
name|getRootQueueMetrics
argument_list|()
decl_stmt|;
name|QueueMetrics
name|userMetrics
init|=
name|metrics
operator|.
name|getUserMetrics
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|this
operator|.
name|userMetricsAvailable
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|userMetrics
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|userMetricsAvailable
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|appsSubmitted
operator|=
name|userMetrics
operator|.
name|getAppsSubmitted
argument_list|()
expr_stmt|;
name|this
operator|.
name|appsCompleted
operator|=
name|metrics
operator|.
name|getAppsCompleted
argument_list|()
expr_stmt|;
name|this
operator|.
name|appsPending
operator|=
name|metrics
operator|.
name|getAppsPending
argument_list|()
expr_stmt|;
name|this
operator|.
name|appsRunning
operator|=
name|metrics
operator|.
name|getAppsRunning
argument_list|()
expr_stmt|;
name|this
operator|.
name|appsFailed
operator|=
name|metrics
operator|.
name|getAppsFailed
argument_list|()
expr_stmt|;
name|this
operator|.
name|appsKilled
operator|=
name|metrics
operator|.
name|getAppsKilled
argument_list|()
expr_stmt|;
name|this
operator|.
name|runningContainers
operator|=
name|userMetrics
operator|.
name|getAllocatedContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|pendingContainers
operator|=
name|userMetrics
operator|.
name|getPendingContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|reservedContainers
operator|=
name|userMetrics
operator|.
name|getReservedContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|reservedMB
operator|=
name|userMetrics
operator|.
name|getReservedMB
argument_list|()
expr_stmt|;
name|this
operator|.
name|pendingMB
operator|=
name|userMetrics
operator|.
name|getPendingMB
argument_list|()
expr_stmt|;
name|this
operator|.
name|allocatedMB
operator|=
name|userMetrics
operator|.
name|getAllocatedMB
argument_list|()
expr_stmt|;
name|this
operator|.
name|reservedVirtualCores
operator|=
name|userMetrics
operator|.
name|getReservedVirtualCores
argument_list|()
expr_stmt|;
name|this
operator|.
name|pendingVirtualCores
operator|=
name|userMetrics
operator|.
name|getPendingVirtualCores
argument_list|()
expr_stmt|;
name|this
operator|.
name|allocatedVirtualCores
operator|=
name|userMetrics
operator|.
name|getAllocatedVirtualCores
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|metricsAvailable ()
specifier|public
name|boolean
name|metricsAvailable
parameter_list|()
block|{
return|return
name|userMetricsAvailable
return|;
block|}
DECL|method|getAppsSubmitted ()
specifier|public
name|int
name|getAppsSubmitted
parameter_list|()
block|{
return|return
name|this
operator|.
name|appsSubmitted
return|;
block|}
DECL|method|getAppsCompleted ()
specifier|public
name|int
name|getAppsCompleted
parameter_list|()
block|{
return|return
name|appsCompleted
return|;
block|}
DECL|method|getAppsPending ()
specifier|public
name|int
name|getAppsPending
parameter_list|()
block|{
return|return
name|appsPending
return|;
block|}
DECL|method|getAppsRunning ()
specifier|public
name|int
name|getAppsRunning
parameter_list|()
block|{
return|return
name|appsRunning
return|;
block|}
DECL|method|getAppsFailed ()
specifier|public
name|int
name|getAppsFailed
parameter_list|()
block|{
return|return
name|appsFailed
return|;
block|}
DECL|method|getAppsKilled ()
specifier|public
name|int
name|getAppsKilled
parameter_list|()
block|{
return|return
name|appsKilled
return|;
block|}
DECL|method|getReservedMB ()
specifier|public
name|long
name|getReservedMB
parameter_list|()
block|{
return|return
name|this
operator|.
name|reservedMB
return|;
block|}
DECL|method|getAllocatedMB ()
specifier|public
name|long
name|getAllocatedMB
parameter_list|()
block|{
return|return
name|this
operator|.
name|allocatedMB
return|;
block|}
DECL|method|getPendingMB ()
specifier|public
name|long
name|getPendingMB
parameter_list|()
block|{
return|return
name|this
operator|.
name|pendingMB
return|;
block|}
DECL|method|getReservedVirtualCores ()
specifier|public
name|long
name|getReservedVirtualCores
parameter_list|()
block|{
return|return
name|this
operator|.
name|reservedVirtualCores
return|;
block|}
DECL|method|getAllocatedVirtualCores ()
specifier|public
name|long
name|getAllocatedVirtualCores
parameter_list|()
block|{
return|return
name|this
operator|.
name|allocatedVirtualCores
return|;
block|}
DECL|method|getPendingVirtualCores ()
specifier|public
name|long
name|getPendingVirtualCores
parameter_list|()
block|{
return|return
name|this
operator|.
name|pendingVirtualCores
return|;
block|}
DECL|method|getReservedContainers ()
specifier|public
name|int
name|getReservedContainers
parameter_list|()
block|{
return|return
name|this
operator|.
name|reservedContainers
return|;
block|}
DECL|method|getRunningContainers ()
specifier|public
name|int
name|getRunningContainers
parameter_list|()
block|{
return|return
name|this
operator|.
name|runningContainers
return|;
block|}
DECL|method|getPendingContainers ()
specifier|public
name|int
name|getPendingContainers
parameter_list|()
block|{
return|return
name|this
operator|.
name|pendingContainers
return|;
block|}
block|}
end_class

end_unit

