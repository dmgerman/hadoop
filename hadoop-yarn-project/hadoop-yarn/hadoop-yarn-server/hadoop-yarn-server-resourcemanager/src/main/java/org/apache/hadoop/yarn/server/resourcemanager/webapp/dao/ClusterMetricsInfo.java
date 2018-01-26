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
name|capacity
operator|.
name|CapacityScheduler
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
specifier|private
name|int
name|appsSubmitted
decl_stmt|;
DECL|field|appsCompleted
specifier|private
name|int
name|appsCompleted
decl_stmt|;
DECL|field|appsPending
specifier|private
name|int
name|appsPending
decl_stmt|;
DECL|field|appsRunning
specifier|private
name|int
name|appsRunning
decl_stmt|;
DECL|field|appsFailed
specifier|private
name|int
name|appsFailed
decl_stmt|;
DECL|field|appsKilled
specifier|private
name|int
name|appsKilled
decl_stmt|;
DECL|field|reservedMB
specifier|private
name|long
name|reservedMB
decl_stmt|;
DECL|field|availableMB
specifier|private
name|long
name|availableMB
decl_stmt|;
DECL|field|allocatedMB
specifier|private
name|long
name|allocatedMB
decl_stmt|;
DECL|field|reservedVirtualCores
specifier|private
name|long
name|reservedVirtualCores
decl_stmt|;
DECL|field|availableVirtualCores
specifier|private
name|long
name|availableVirtualCores
decl_stmt|;
DECL|field|allocatedVirtualCores
specifier|private
name|long
name|allocatedVirtualCores
decl_stmt|;
DECL|field|containersAllocated
specifier|private
name|int
name|containersAllocated
decl_stmt|;
DECL|field|containersReserved
specifier|private
name|int
name|containersReserved
decl_stmt|;
DECL|field|containersPending
specifier|private
name|int
name|containersPending
decl_stmt|;
DECL|field|totalMB
specifier|private
name|long
name|totalMB
decl_stmt|;
DECL|field|totalVirtualCores
specifier|private
name|long
name|totalVirtualCores
decl_stmt|;
DECL|field|totalNodes
specifier|private
name|int
name|totalNodes
decl_stmt|;
DECL|field|lostNodes
specifier|private
name|int
name|lostNodes
decl_stmt|;
DECL|field|unhealthyNodes
specifier|private
name|int
name|unhealthyNodes
decl_stmt|;
DECL|field|decommissioningNodes
specifier|private
name|int
name|decommissioningNodes
decl_stmt|;
DECL|field|decommissionedNodes
specifier|private
name|int
name|decommissionedNodes
decl_stmt|;
DECL|field|rebootedNodes
specifier|private
name|int
name|rebootedNodes
decl_stmt|;
DECL|field|activeNodes
specifier|private
name|int
name|activeNodes
decl_stmt|;
DECL|field|shutdownNodes
specifier|private
name|int
name|shutdownNodes
decl_stmt|;
comment|// Total used resource of the cluster, including all partitions
DECL|field|totalUsedResourcesAcrossPartition
specifier|private
name|ResourceInfo
name|totalUsedResourcesAcrossPartition
decl_stmt|;
comment|// Total registered resources of the cluster, including all partitions
DECL|field|totalClusterResourcesAcrossPartition
specifier|private
name|ResourceInfo
name|totalClusterResourcesAcrossPartition
decl_stmt|;
DECL|method|ClusterMetricsInfo ()
specifier|public
name|ClusterMetricsInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|ClusterMetricsInfo (final ResourceManager rm)
specifier|public
name|ClusterMetricsInfo
parameter_list|(
specifier|final
name|ResourceManager
name|rm
parameter_list|)
block|{
name|this
argument_list|(
name|rm
operator|.
name|getResourceScheduler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ClusterMetricsInfo (final ResourceScheduler rs)
specifier|public
name|ClusterMetricsInfo
parameter_list|(
specifier|final
name|ResourceScheduler
name|rs
parameter_list|)
block|{
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
name|reservedVirtualCores
operator|=
name|metrics
operator|.
name|getReservedVirtualCores
argument_list|()
expr_stmt|;
name|this
operator|.
name|availableVirtualCores
operator|=
name|metrics
operator|.
name|getAvailableVirtualCores
argument_list|()
expr_stmt|;
name|this
operator|.
name|allocatedVirtualCores
operator|=
name|metrics
operator|.
name|getAllocatedVirtualCores
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
name|containersPending
operator|=
name|metrics
operator|.
name|getPendingContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|containersReserved
operator|=
name|metrics
operator|.
name|getReservedContainers
argument_list|()
expr_stmt|;
if|if
condition|(
name|rs
operator|instanceof
name|CapacityScheduler
condition|)
block|{
name|CapacityScheduler
name|cs
init|=
operator|(
name|CapacityScheduler
operator|)
name|rs
decl_stmt|;
name|this
operator|.
name|totalMB
operator|=
name|availableMB
operator|+
name|allocatedMB
operator|+
name|reservedMB
expr_stmt|;
name|this
operator|.
name|totalVirtualCores
operator|=
name|availableVirtualCores
operator|+
name|allocatedVirtualCores
operator|+
name|containersReserved
expr_stmt|;
comment|// TODO, add support of other schedulers to get total used resources
comment|// across partition.
if|if
condition|(
name|cs
operator|.
name|getRootQueue
argument_list|()
operator|!=
literal|null
operator|&&
name|cs
operator|.
name|getRootQueue
argument_list|()
operator|.
name|getQueueResourceUsage
argument_list|()
operator|!=
literal|null
operator|&&
name|cs
operator|.
name|getRootQueue
argument_list|()
operator|.
name|getQueueResourceUsage
argument_list|()
operator|.
name|getAllUsed
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|totalUsedResourcesAcrossPartition
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|cs
operator|.
name|getRootQueue
argument_list|()
operator|.
name|getQueueResourceUsage
argument_list|()
operator|.
name|getAllUsed
argument_list|()
argument_list|)
expr_stmt|;
name|totalClusterResourcesAcrossPartition
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|cs
operator|.
name|getClusterResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|totalMB
operator|=
name|availableMB
operator|+
name|allocatedMB
expr_stmt|;
name|this
operator|.
name|totalVirtualCores
operator|=
name|availableVirtualCores
operator|+
name|allocatedVirtualCores
expr_stmt|;
block|}
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
name|decommissioningNodes
operator|=
name|clusterMetrics
operator|.
name|getNumDecommissioningNMs
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
name|shutdownNodes
operator|=
name|clusterMetrics
operator|.
name|getNumShutdownNMs
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
operator|+
name|unhealthyNodes
operator|+
name|decommissioningNodes
operator|+
name|shutdownNodes
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
DECL|method|getAvailableVirtualCores ()
specifier|public
name|long
name|getAvailableVirtualCores
parameter_list|()
block|{
return|return
name|this
operator|.
name|availableVirtualCores
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
DECL|method|getReservedContainers ()
specifier|public
name|int
name|getReservedContainers
parameter_list|()
block|{
return|return
name|this
operator|.
name|containersReserved
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
name|containersPending
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
DECL|method|getTotalVirtualCores ()
specifier|public
name|long
name|getTotalVirtualCores
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalVirtualCores
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
DECL|method|getDecommissioningNodes ()
specifier|public
name|int
name|getDecommissioningNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|decommissioningNodes
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
DECL|method|getShutdownNodes ()
specifier|public
name|int
name|getShutdownNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|shutdownNodes
return|;
block|}
DECL|method|setContainersReserved (int containersReserved)
specifier|public
name|void
name|setContainersReserved
parameter_list|(
name|int
name|containersReserved
parameter_list|)
block|{
name|this
operator|.
name|containersReserved
operator|=
name|containersReserved
expr_stmt|;
block|}
DECL|method|setContainersPending (int containersPending)
specifier|public
name|void
name|setContainersPending
parameter_list|(
name|int
name|containersPending
parameter_list|)
block|{
name|this
operator|.
name|containersPending
operator|=
name|containersPending
expr_stmt|;
block|}
DECL|method|setAppsSubmitted (int appsSubmitted)
specifier|public
name|void
name|setAppsSubmitted
parameter_list|(
name|int
name|appsSubmitted
parameter_list|)
block|{
name|this
operator|.
name|appsSubmitted
operator|=
name|appsSubmitted
expr_stmt|;
block|}
DECL|method|setAppsCompleted (int appsCompleted)
specifier|public
name|void
name|setAppsCompleted
parameter_list|(
name|int
name|appsCompleted
parameter_list|)
block|{
name|this
operator|.
name|appsCompleted
operator|=
name|appsCompleted
expr_stmt|;
block|}
DECL|method|setAppsPending (int appsPending)
specifier|public
name|void
name|setAppsPending
parameter_list|(
name|int
name|appsPending
parameter_list|)
block|{
name|this
operator|.
name|appsPending
operator|=
name|appsPending
expr_stmt|;
block|}
DECL|method|setAppsRunning (int appsRunning)
specifier|public
name|void
name|setAppsRunning
parameter_list|(
name|int
name|appsRunning
parameter_list|)
block|{
name|this
operator|.
name|appsRunning
operator|=
name|appsRunning
expr_stmt|;
block|}
DECL|method|setAppsFailed (int appsFailed)
specifier|public
name|void
name|setAppsFailed
parameter_list|(
name|int
name|appsFailed
parameter_list|)
block|{
name|this
operator|.
name|appsFailed
operator|=
name|appsFailed
expr_stmt|;
block|}
DECL|method|setAppsKilled (int appsKilled)
specifier|public
name|void
name|setAppsKilled
parameter_list|(
name|int
name|appsKilled
parameter_list|)
block|{
name|this
operator|.
name|appsKilled
operator|=
name|appsKilled
expr_stmt|;
block|}
DECL|method|setReservedMB (long reservedMB)
specifier|public
name|void
name|setReservedMB
parameter_list|(
name|long
name|reservedMB
parameter_list|)
block|{
name|this
operator|.
name|reservedMB
operator|=
name|reservedMB
expr_stmt|;
block|}
DECL|method|setAvailableMB (long availableMB)
specifier|public
name|void
name|setAvailableMB
parameter_list|(
name|long
name|availableMB
parameter_list|)
block|{
name|this
operator|.
name|availableMB
operator|=
name|availableMB
expr_stmt|;
block|}
DECL|method|setAllocatedMB (long allocatedMB)
specifier|public
name|void
name|setAllocatedMB
parameter_list|(
name|long
name|allocatedMB
parameter_list|)
block|{
name|this
operator|.
name|allocatedMB
operator|=
name|allocatedMB
expr_stmt|;
block|}
DECL|method|setReservedVirtualCores (long reservedVirtualCores)
specifier|public
name|void
name|setReservedVirtualCores
parameter_list|(
name|long
name|reservedVirtualCores
parameter_list|)
block|{
name|this
operator|.
name|reservedVirtualCores
operator|=
name|reservedVirtualCores
expr_stmt|;
block|}
DECL|method|setAvailableVirtualCores (long availableVirtualCores)
specifier|public
name|void
name|setAvailableVirtualCores
parameter_list|(
name|long
name|availableVirtualCores
parameter_list|)
block|{
name|this
operator|.
name|availableVirtualCores
operator|=
name|availableVirtualCores
expr_stmt|;
block|}
DECL|method|setAllocatedVirtualCores (long allocatedVirtualCores)
specifier|public
name|void
name|setAllocatedVirtualCores
parameter_list|(
name|long
name|allocatedVirtualCores
parameter_list|)
block|{
name|this
operator|.
name|allocatedVirtualCores
operator|=
name|allocatedVirtualCores
expr_stmt|;
block|}
DECL|method|setContainersAllocated (int containersAllocated)
specifier|public
name|void
name|setContainersAllocated
parameter_list|(
name|int
name|containersAllocated
parameter_list|)
block|{
name|this
operator|.
name|containersAllocated
operator|=
name|containersAllocated
expr_stmt|;
block|}
DECL|method|setTotalMB (long totalMB)
specifier|public
name|void
name|setTotalMB
parameter_list|(
name|long
name|totalMB
parameter_list|)
block|{
name|this
operator|.
name|totalMB
operator|=
name|totalMB
expr_stmt|;
block|}
DECL|method|setTotalVirtualCores (long totalVirtualCores)
specifier|public
name|void
name|setTotalVirtualCores
parameter_list|(
name|long
name|totalVirtualCores
parameter_list|)
block|{
name|this
operator|.
name|totalVirtualCores
operator|=
name|totalVirtualCores
expr_stmt|;
block|}
DECL|method|setTotalNodes (int totalNodes)
specifier|public
name|void
name|setTotalNodes
parameter_list|(
name|int
name|totalNodes
parameter_list|)
block|{
name|this
operator|.
name|totalNodes
operator|=
name|totalNodes
expr_stmt|;
block|}
DECL|method|setLostNodes (int lostNodes)
specifier|public
name|void
name|setLostNodes
parameter_list|(
name|int
name|lostNodes
parameter_list|)
block|{
name|this
operator|.
name|lostNodes
operator|=
name|lostNodes
expr_stmt|;
block|}
DECL|method|setUnhealthyNodes (int unhealthyNodes)
specifier|public
name|void
name|setUnhealthyNodes
parameter_list|(
name|int
name|unhealthyNodes
parameter_list|)
block|{
name|this
operator|.
name|unhealthyNodes
operator|=
name|unhealthyNodes
expr_stmt|;
block|}
DECL|method|setDecommissioningNodes (int decommissioningNodes)
specifier|public
name|void
name|setDecommissioningNodes
parameter_list|(
name|int
name|decommissioningNodes
parameter_list|)
block|{
name|this
operator|.
name|decommissioningNodes
operator|=
name|decommissioningNodes
expr_stmt|;
block|}
DECL|method|setDecommissionedNodes (int decommissionedNodes)
specifier|public
name|void
name|setDecommissionedNodes
parameter_list|(
name|int
name|decommissionedNodes
parameter_list|)
block|{
name|this
operator|.
name|decommissionedNodes
operator|=
name|decommissionedNodes
expr_stmt|;
block|}
DECL|method|setRebootedNodes (int rebootedNodes)
specifier|public
name|void
name|setRebootedNodes
parameter_list|(
name|int
name|rebootedNodes
parameter_list|)
block|{
name|this
operator|.
name|rebootedNodes
operator|=
name|rebootedNodes
expr_stmt|;
block|}
DECL|method|setActiveNodes (int activeNodes)
specifier|public
name|void
name|setActiveNodes
parameter_list|(
name|int
name|activeNodes
parameter_list|)
block|{
name|this
operator|.
name|activeNodes
operator|=
name|activeNodes
expr_stmt|;
block|}
DECL|method|setShutdownNodes (int shutdownNodes)
specifier|public
name|void
name|setShutdownNodes
parameter_list|(
name|int
name|shutdownNodes
parameter_list|)
block|{
name|this
operator|.
name|shutdownNodes
operator|=
name|shutdownNodes
expr_stmt|;
block|}
DECL|method|getTotalUsedResourcesAcrossPartition ()
specifier|public
name|ResourceInfo
name|getTotalUsedResourcesAcrossPartition
parameter_list|()
block|{
return|return
name|totalUsedResourcesAcrossPartition
return|;
block|}
DECL|method|getTotalClusterResourcesAcrossPartition ()
specifier|public
name|ResourceInfo
name|getTotalClusterResourcesAcrossPartition
parameter_list|()
block|{
return|return
name|totalClusterResourcesAcrossPartition
return|;
block|}
block|}
end_class

end_unit

