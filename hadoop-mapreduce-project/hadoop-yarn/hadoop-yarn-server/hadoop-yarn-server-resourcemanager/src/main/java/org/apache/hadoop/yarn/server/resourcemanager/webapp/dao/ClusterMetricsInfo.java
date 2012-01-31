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
name|ClusterMetrics
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
literal|"clusterMetrics"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|ClusterMetricsInfo
specifier|public
class|class
name|ClusterMetricsInfo
block|{
DECL|field|appsSubmitted
specifier|protected
name|int
name|appsSubmitted
decl_stmt|;
DECL|field|reservedMB
specifier|protected
name|long
name|reservedMB
decl_stmt|;
DECL|field|availableMB
specifier|protected
name|long
name|availableMB
decl_stmt|;
DECL|field|allocatedMB
specifier|protected
name|long
name|allocatedMB
decl_stmt|;
DECL|field|containersAllocated
specifier|protected
name|int
name|containersAllocated
decl_stmt|;
DECL|field|totalMB
specifier|protected
name|long
name|totalMB
decl_stmt|;
DECL|field|totalNodes
specifier|protected
name|int
name|totalNodes
decl_stmt|;
DECL|field|lostNodes
specifier|protected
name|int
name|lostNodes
decl_stmt|;
DECL|field|unhealthyNodes
specifier|protected
name|int
name|unhealthyNodes
decl_stmt|;
DECL|field|decommissionedNodes
specifier|protected
name|int
name|decommissionedNodes
decl_stmt|;
DECL|field|rebootedNodes
specifier|protected
name|int
name|rebootedNodes
decl_stmt|;
DECL|field|activeNodes
specifier|protected
name|int
name|activeNodes
decl_stmt|;
DECL|method|ClusterMetricsInfo ()
specifier|public
name|ClusterMetricsInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|ClusterMetricsInfo (final ResourceManager rm, final RMContext rmContext)
specifier|public
name|ClusterMetricsInfo
parameter_list|(
specifier|final
name|ResourceManager
name|rm
parameter_list|,
specifier|final
name|RMContext
name|rmContext
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
name|ClusterMetrics
name|clusterMetrics
init|=
name|ClusterMetrics
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|this
operator|.
name|appsSubmitted
operator|=
name|metrics
operator|.
name|getAppsSubmitted
argument_list|()
expr_stmt|;
name|this
operator|.
name|reservedMB
operator|=
name|metrics
operator|.
name|getReservedMB
argument_list|()
expr_stmt|;
name|this
operator|.
name|availableMB
operator|=
name|metrics
operator|.
name|getAvailableMB
argument_list|()
expr_stmt|;
name|this
operator|.
name|allocatedMB
operator|=
name|metrics
operator|.
name|getAllocatedMB
argument_list|()
expr_stmt|;
name|this
operator|.
name|containersAllocated
operator|=
name|metrics
operator|.
name|getAllocatedContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|totalMB
operator|=
name|availableMB
operator|+
name|reservedMB
operator|+
name|allocatedMB
expr_stmt|;
name|this
operator|.
name|activeNodes
operator|=
name|clusterMetrics
operator|.
name|getNumActiveNMs
argument_list|()
expr_stmt|;
name|this
operator|.
name|lostNodes
operator|=
name|clusterMetrics
operator|.
name|getNumLostNMs
argument_list|()
expr_stmt|;
name|this
operator|.
name|unhealthyNodes
operator|=
name|clusterMetrics
operator|.
name|getUnhealthyNMs
argument_list|()
expr_stmt|;
name|this
operator|.
name|decommissionedNodes
operator|=
name|clusterMetrics
operator|.
name|getNumDecommisionedNMs
argument_list|()
expr_stmt|;
name|this
operator|.
name|rebootedNodes
operator|=
name|clusterMetrics
operator|.
name|getNumRebootedNMs
argument_list|()
expr_stmt|;
name|this
operator|.
name|totalNodes
operator|=
name|activeNodes
operator|+
name|lostNodes
operator|+
name|decommissionedNodes
operator|+
name|rebootedNodes
expr_stmt|;
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
DECL|method|getAvailableMB ()
specifier|public
name|long
name|getAvailableMB
parameter_list|()
block|{
return|return
name|this
operator|.
name|availableMB
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
DECL|method|getContainersAllocated ()
specifier|public
name|int
name|getContainersAllocated
parameter_list|()
block|{
return|return
name|this
operator|.
name|containersAllocated
return|;
block|}
DECL|method|getTotalMB ()
specifier|public
name|long
name|getTotalMB
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalMB
return|;
block|}
DECL|method|getTotalNodes ()
specifier|public
name|int
name|getTotalNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalNodes
return|;
block|}
DECL|method|getActiveNodes ()
specifier|public
name|int
name|getActiveNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|activeNodes
return|;
block|}
DECL|method|getLostNodes ()
specifier|public
name|int
name|getLostNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|lostNodes
return|;
block|}
DECL|method|getRebootedNodes ()
specifier|public
name|int
name|getRebootedNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|rebootedNodes
return|;
block|}
DECL|method|getUnhealthyNodes ()
specifier|public
name|int
name|getUnhealthyNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|unhealthyNodes
return|;
block|}
DECL|method|getDecommissionedNodes ()
specifier|public
name|int
name|getDecommissionedNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|decommissionedNodes
return|;
block|}
block|}
end_class

end_unit

