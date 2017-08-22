begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Stable
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|util
operator|.
name|Records
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Contains various scheduling metrics to be reported by UI and CLI.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|ApplicationResourceUsageReport
specifier|public
specifier|abstract
class|class
name|ApplicationResourceUsageReport
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance ( int numUsedContainers, int numReservedContainers, Resource usedResources, Resource reservedResources, Resource neededResources, Map<String, Long> resourceSecondsMap, float queueUsagePerc, float clusterUsagePerc, Map<String, Long> preemtedResourceSecondsMap)
specifier|public
specifier|static
name|ApplicationResourceUsageReport
name|newInstance
parameter_list|(
name|int
name|numUsedContainers
parameter_list|,
name|int
name|numReservedContainers
parameter_list|,
name|Resource
name|usedResources
parameter_list|,
name|Resource
name|reservedResources
parameter_list|,
name|Resource
name|neededResources
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|resourceSecondsMap
parameter_list|,
name|float
name|queueUsagePerc
parameter_list|,
name|float
name|clusterUsagePerc
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|preemtedResourceSecondsMap
parameter_list|)
block|{
name|ApplicationResourceUsageReport
name|report
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationResourceUsageReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|report
operator|.
name|setNumUsedContainers
argument_list|(
name|numUsedContainers
argument_list|)
expr_stmt|;
name|report
operator|.
name|setNumReservedContainers
argument_list|(
name|numReservedContainers
argument_list|)
expr_stmt|;
name|report
operator|.
name|setUsedResources
argument_list|(
name|usedResources
argument_list|)
expr_stmt|;
name|report
operator|.
name|setReservedResources
argument_list|(
name|reservedResources
argument_list|)
expr_stmt|;
name|report
operator|.
name|setNeededResources
argument_list|(
name|neededResources
argument_list|)
expr_stmt|;
name|report
operator|.
name|setResourceSecondsMap
argument_list|(
name|resourceSecondsMap
argument_list|)
expr_stmt|;
name|report
operator|.
name|setQueueUsagePercentage
argument_list|(
name|queueUsagePerc
argument_list|)
expr_stmt|;
name|report
operator|.
name|setClusterUsagePercentage
argument_list|(
name|clusterUsagePerc
argument_list|)
expr_stmt|;
name|report
operator|.
name|setPreemptedResourceSecondsMap
argument_list|(
name|preemtedResourceSecondsMap
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
comment|/**    * Get the number of used containers.  -1 for invalid/inaccessible reports.    * @return the number of used containers    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNumUsedContainers ()
specifier|public
specifier|abstract
name|int
name|getNumUsedContainers
parameter_list|()
function_decl|;
comment|/**    * Set the number of used containers    * @param num_containers the number of used containers    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumUsedContainers (int num_containers)
specifier|public
specifier|abstract
name|void
name|setNumUsedContainers
parameter_list|(
name|int
name|num_containers
parameter_list|)
function_decl|;
comment|/**    * Get the number of reserved containers.  -1 for invalid/inaccessible reports.    * @return the number of reserved containers    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getNumReservedContainers ()
specifier|public
specifier|abstract
name|int
name|getNumReservedContainers
parameter_list|()
function_decl|;
comment|/**    * Set the number of reserved containers    * @param num_reserved_containers the number of reserved containers    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumReservedContainers (int num_reserved_containers)
specifier|public
specifier|abstract
name|void
name|setNumReservedContainers
parameter_list|(
name|int
name|num_reserved_containers
parameter_list|)
function_decl|;
comment|/**    * Get the used<code>Resource</code>.  -1 for invalid/inaccessible reports.    * @return the used<code>Resource</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getUsedResources ()
specifier|public
specifier|abstract
name|Resource
name|getUsedResources
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setUsedResources (Resource resources)
specifier|public
specifier|abstract
name|void
name|setUsedResources
parameter_list|(
name|Resource
name|resources
parameter_list|)
function_decl|;
comment|/**    * Get the reserved<code>Resource</code>.  -1 for invalid/inaccessible reports.    * @return the reserved<code>Resource</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getReservedResources ()
specifier|public
specifier|abstract
name|Resource
name|getReservedResources
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setReservedResources (Resource reserved_resources)
specifier|public
specifier|abstract
name|void
name|setReservedResources
parameter_list|(
name|Resource
name|reserved_resources
parameter_list|)
function_decl|;
comment|/**    * Get the needed<code>Resource</code>.  -1 for invalid/inaccessible reports.    * @return the needed<code>Resource</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNeededResources ()
specifier|public
specifier|abstract
name|Resource
name|getNeededResources
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNeededResources (Resource needed_resources)
specifier|public
specifier|abstract
name|void
name|setNeededResources
parameter_list|(
name|Resource
name|needed_resources
parameter_list|)
function_decl|;
comment|/**    * Set the aggregated amount of memory (in megabytes) the application has    * allocated times the number of seconds the application has been running.    * @param memory_seconds the aggregated amount of memory seconds    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setMemorySeconds (long memory_seconds)
specifier|public
specifier|abstract
name|void
name|setMemorySeconds
parameter_list|(
name|long
name|memory_seconds
parameter_list|)
function_decl|;
comment|/**    * Get the aggregated amount of memory (in megabytes) the application has    * allocated times the number of seconds the application has been running.    * @return the aggregated amount of memory seconds    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getMemorySeconds ()
specifier|public
specifier|abstract
name|long
name|getMemorySeconds
parameter_list|()
function_decl|;
comment|/**    * Set the aggregated number of vcores that the application has allocated    * times the number of seconds the application has been running.    * @param vcore_seconds the aggregated number of vcore seconds    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setVcoreSeconds (long vcore_seconds)
specifier|public
specifier|abstract
name|void
name|setVcoreSeconds
parameter_list|(
name|long
name|vcore_seconds
parameter_list|)
function_decl|;
comment|/**    * Get the aggregated number of vcores that the application has allocated    * times the number of seconds the application has been running.    * @return the aggregated number of vcore seconds    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getVcoreSeconds ()
specifier|public
specifier|abstract
name|long
name|getVcoreSeconds
parameter_list|()
function_decl|;
comment|/**    * Get the percentage of resources of the queue that the app is using.    * @return the percentage of resources of the queue that the app is using.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueueUsagePercentage ()
specifier|public
specifier|abstract
name|float
name|getQueueUsagePercentage
parameter_list|()
function_decl|;
comment|/**    * Set the percentage of resources of the queue that the app is using.    * @param queueUsagePerc the percentage of resources of the queue that    *                       the app is using.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setQueueUsagePercentage (float queueUsagePerc)
specifier|public
specifier|abstract
name|void
name|setQueueUsagePercentage
parameter_list|(
name|float
name|queueUsagePerc
parameter_list|)
function_decl|;
comment|/**    * Get the percentage of resources of the cluster that the app is using.    * @return the percentage of resources of the cluster that the app is using.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getClusterUsagePercentage ()
specifier|public
specifier|abstract
name|float
name|getClusterUsagePercentage
parameter_list|()
function_decl|;
comment|/**    * Set the percentage of resources of the cluster that the app is using.    * @param clusterUsagePerc the percentage of resources of the cluster that    *                         the app is using.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setClusterUsagePercentage (float clusterUsagePerc)
specifier|public
specifier|abstract
name|void
name|setClusterUsagePercentage
parameter_list|(
name|float
name|clusterUsagePerc
parameter_list|)
function_decl|;
comment|/**    * Set the aggregated amount of memory preempted (in megabytes)    * the application has allocated times the number of seconds    * the application has been running.    * @param memorySeconds the aggregated amount of memory seconds    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setPreemptedMemorySeconds (long memorySeconds)
specifier|public
specifier|abstract
name|void
name|setPreemptedMemorySeconds
parameter_list|(
name|long
name|memorySeconds
parameter_list|)
function_decl|;
comment|/**    * Get the aggregated amount of memory preempted(in megabytes)    * the application has allocated times the number of    * seconds the application has been running.    * @return the aggregated amount of memory seconds    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getPreemptedMemorySeconds ()
specifier|public
specifier|abstract
name|long
name|getPreemptedMemorySeconds
parameter_list|()
function_decl|;
comment|/**    * Set the aggregated number of vcores preempted that the application has    * allocated times the number of seconds the application has been running.    * @param vcoreSeconds the aggregated number of vcore seconds    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setPreemptedVcoreSeconds (long vcoreSeconds)
specifier|public
specifier|abstract
name|void
name|setPreemptedVcoreSeconds
parameter_list|(
name|long
name|vcoreSeconds
parameter_list|)
function_decl|;
comment|/**    * Get the aggregated number of vcores preempted that the application has    * allocated times the number of seconds the application has been running.    * @return the aggregated number of vcore seconds    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getPreemptedVcoreSeconds ()
specifier|public
specifier|abstract
name|long
name|getPreemptedVcoreSeconds
parameter_list|()
function_decl|;
comment|/**    * Get the aggregated number of resources that the application has    * allocated times the number of seconds the application has been running.    * @return map containing the resource name and aggregated resource-seconds    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getResourceSecondsMap ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getResourceSecondsMap
parameter_list|()
function_decl|;
comment|/**    * Set the aggregated number of resources that the application has    * allocated times the number of seconds the application has been running.    * @param resourceSecondsMap map containing the resource name and aggregated    *                           resource-seconds    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setResourceSecondsMap ( Map<String, Long> resourceSecondsMap)
specifier|public
specifier|abstract
name|void
name|setResourceSecondsMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|resourceSecondsMap
parameter_list|)
function_decl|;
comment|/**    * Get the aggregated number of resources preempted that the application has    * allocated times the number of seconds the application has been running.    * @return map containing the resource name and aggregated preempted    * resource-seconds    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getPreemptedResourceSecondsMap ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getPreemptedResourceSecondsMap
parameter_list|()
function_decl|;
comment|/**    * Set the aggregated number of resources preempted that the application has    * allocated times the number of seconds the application has been running.    * @param preemptedResourceSecondsMap  map containing the resource name and    *                                     aggregated preempted resource-seconds    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setPreemptedResourceSecondsMap ( Map<String, Long> preemptedResourceSecondsMap)
specifier|public
specifier|abstract
name|void
name|setPreemptedResourceSecondsMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|preemptedResourceSecondsMap
parameter_list|)
function_decl|;
block|}
end_class

end_unit

