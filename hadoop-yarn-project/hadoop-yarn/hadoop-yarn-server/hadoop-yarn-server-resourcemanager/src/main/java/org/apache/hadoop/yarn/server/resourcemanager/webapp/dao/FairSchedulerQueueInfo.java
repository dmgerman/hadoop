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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|XmlSeeAlso
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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|fair
operator|.
name|AllocationConfiguration
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
name|fair
operator|.
name|FSLeafQueue
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
name|fair
operator|.
name|FSQueue
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
name|fair
operator|.
name|FairScheduler
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
name|resource
operator|.
name|Resources
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|XmlSeeAlso
argument_list|(
block|{
name|FairSchedulerLeafQueueInfo
operator|.
name|class
block|}
argument_list|)
DECL|class|FairSchedulerQueueInfo
specifier|public
class|class
name|FairSchedulerQueueInfo
block|{
DECL|field|maxApps
specifier|private
name|int
name|maxApps
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|fractionMemUsed
specifier|private
name|float
name|fractionMemUsed
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|fractionMemSteadyFairShare
specifier|private
name|float
name|fractionMemSteadyFairShare
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|fractionMemFairShare
specifier|private
name|float
name|fractionMemFairShare
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|fractionMemMaxShare
specifier|private
name|float
name|fractionMemMaxShare
decl_stmt|;
DECL|field|minResources
specifier|private
name|ResourceInfo
name|minResources
decl_stmt|;
DECL|field|maxResources
specifier|private
name|ResourceInfo
name|maxResources
decl_stmt|;
DECL|field|usedResources
specifier|private
name|ResourceInfo
name|usedResources
decl_stmt|;
DECL|field|amUsedResources
specifier|private
name|ResourceInfo
name|amUsedResources
decl_stmt|;
DECL|field|amMaxResources
specifier|private
name|ResourceInfo
name|amMaxResources
decl_stmt|;
DECL|field|demandResources
specifier|private
name|ResourceInfo
name|demandResources
decl_stmt|;
DECL|field|steadyFairResources
specifier|private
name|ResourceInfo
name|steadyFairResources
decl_stmt|;
DECL|field|fairResources
specifier|private
name|ResourceInfo
name|fairResources
decl_stmt|;
DECL|field|clusterResources
specifier|private
name|ResourceInfo
name|clusterResources
decl_stmt|;
DECL|field|reservedResources
specifier|private
name|ResourceInfo
name|reservedResources
decl_stmt|;
DECL|field|maxContainerAllocation
specifier|private
name|ResourceInfo
name|maxContainerAllocation
decl_stmt|;
DECL|field|allocatedContainers
specifier|private
name|long
name|allocatedContainers
decl_stmt|;
DECL|field|reservedContainers
specifier|private
name|long
name|reservedContainers
decl_stmt|;
DECL|field|queueName
specifier|private
name|String
name|queueName
decl_stmt|;
DECL|field|schedulingPolicy
specifier|private
name|String
name|schedulingPolicy
decl_stmt|;
DECL|field|preemptable
specifier|private
name|boolean
name|preemptable
decl_stmt|;
DECL|field|childQueues
specifier|private
name|FairSchedulerQueueInfoList
name|childQueues
decl_stmt|;
DECL|method|FairSchedulerQueueInfo ()
specifier|public
name|FairSchedulerQueueInfo
parameter_list|()
block|{   }
DECL|method|FairSchedulerQueueInfo (FSQueue queue, FairScheduler scheduler)
specifier|public
name|FairSchedulerQueueInfo
parameter_list|(
name|FSQueue
name|queue
parameter_list|,
name|FairScheduler
name|scheduler
parameter_list|)
block|{
name|AllocationConfiguration
name|allocConf
init|=
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
decl_stmt|;
name|queueName
operator|=
name|queue
operator|.
name|getName
argument_list|()
expr_stmt|;
name|schedulingPolicy
operator|=
name|queue
operator|.
name|getPolicy
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|clusterResources
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|scheduler
operator|.
name|getClusterResource
argument_list|()
argument_list|)
expr_stmt|;
name|amUsedResources
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|getAMResourceUsageMB
argument_list|()
argument_list|,
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|getAMResourceUsageVCores
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|amMaxResources
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|getMaxAMShareMB
argument_list|()
argument_list|,
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|getMaxAMShareVCores
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|usedResources
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|queue
operator|.
name|getResourceUsage
argument_list|()
argument_list|)
expr_stmt|;
name|demandResources
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|queue
operator|.
name|getDemand
argument_list|()
argument_list|)
expr_stmt|;
name|fractionMemUsed
operator|=
operator|(
name|float
operator|)
name|usedResources
operator|.
name|getMemorySize
argument_list|()
operator|/
name|clusterResources
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|steadyFairResources
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|queue
operator|.
name|getSteadyFairShare
argument_list|()
argument_list|)
expr_stmt|;
name|fairResources
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|queue
operator|.
name|getFairShare
argument_list|()
argument_list|)
expr_stmt|;
name|minResources
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|queue
operator|.
name|getMinShare
argument_list|()
argument_list|)
expr_stmt|;
name|maxResources
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|Resources
operator|.
name|componentwiseMin
argument_list|(
name|queue
operator|.
name|getMaxShare
argument_list|()
argument_list|,
name|scheduler
operator|.
name|getClusterResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|maxContainerAllocation
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|scheduler
operator|.
name|getMaximumResourceCapability
argument_list|(
name|queueName
argument_list|)
argument_list|)
expr_stmt|;
name|reservedResources
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|queue
operator|.
name|getReservedResource
argument_list|()
argument_list|)
expr_stmt|;
name|fractionMemSteadyFairShare
operator|=
operator|(
name|float
operator|)
name|steadyFairResources
operator|.
name|getMemorySize
argument_list|()
operator|/
name|clusterResources
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|fractionMemFairShare
operator|=
operator|(
name|float
operator|)
name|fairResources
operator|.
name|getMemorySize
argument_list|()
operator|/
name|clusterResources
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|fractionMemMaxShare
operator|=
operator|(
name|float
operator|)
name|maxResources
operator|.
name|getMemorySize
argument_list|()
operator|/
name|clusterResources
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|maxApps
operator|=
name|queue
operator|.
name|getMaxRunningApps
argument_list|()
expr_stmt|;
name|allocatedContainers
operator|=
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|getAllocatedContainers
argument_list|()
expr_stmt|;
name|reservedContainers
operator|=
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|getReservedContainers
argument_list|()
expr_stmt|;
if|if
condition|(
name|allocConf
operator|.
name|isReservable
argument_list|(
name|queueName
argument_list|)
operator|&&
operator|!
name|allocConf
operator|.
name|getShowReservationAsQueues
argument_list|(
name|queueName
argument_list|)
condition|)
block|{
return|return;
block|}
name|preemptable
operator|=
name|queue
operator|.
name|isPreemptable
argument_list|()
expr_stmt|;
name|childQueues
operator|=
name|getChildQueues
argument_list|(
name|queue
argument_list|,
name|scheduler
argument_list|)
expr_stmt|;
block|}
DECL|method|getAllocatedContainers ()
specifier|public
name|long
name|getAllocatedContainers
parameter_list|()
block|{
return|return
name|allocatedContainers
return|;
block|}
DECL|method|getReservedContainers ()
specifier|public
name|long
name|getReservedContainers
parameter_list|()
block|{
return|return
name|reservedContainers
return|;
block|}
DECL|method|getChildQueues (FSQueue queue, FairScheduler scheduler)
specifier|protected
name|FairSchedulerQueueInfoList
name|getChildQueues
parameter_list|(
name|FSQueue
name|queue
parameter_list|,
name|FairScheduler
name|scheduler
parameter_list|)
block|{
comment|// Return null to omit 'childQueues' field from the return value of
comment|// REST API if it is empty. We omit the field to keep the consistency
comment|// with CapacitySchedulerQueueInfo, which omits 'queues' field if empty.
name|Collection
argument_list|<
name|FSQueue
argument_list|>
name|children
init|=
name|queue
operator|.
name|getChildQueues
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|FairSchedulerQueueInfoList
name|list
init|=
operator|new
name|FairSchedulerQueueInfoList
argument_list|()
decl_stmt|;
for|for
control|(
name|FSQueue
name|child
range|:
name|children
control|)
block|{
if|if
condition|(
name|child
operator|instanceof
name|FSLeafQueue
condition|)
block|{
name|list
operator|.
name|addToQueueInfoList
argument_list|(
operator|new
name|FairSchedulerLeafQueueInfo
argument_list|(
operator|(
name|FSLeafQueue
operator|)
name|child
argument_list|,
name|scheduler
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|list
operator|.
name|addToQueueInfoList
argument_list|(
operator|new
name|FairSchedulerQueueInfo
argument_list|(
name|child
argument_list|,
name|scheduler
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
comment|/**    * Returns the steady fair share as a fraction of the entire cluster capacity.    */
DECL|method|getSteadyFairShareMemoryFraction ()
specifier|public
name|float
name|getSteadyFairShareMemoryFraction
parameter_list|()
block|{
return|return
name|fractionMemSteadyFairShare
return|;
block|}
comment|/**    * Returns the fair share as a fraction of the entire cluster capacity.    */
DECL|method|getFairShareMemoryFraction ()
specifier|public
name|float
name|getFairShareMemoryFraction
parameter_list|()
block|{
return|return
name|fractionMemFairShare
return|;
block|}
comment|/**    * Returns the steady fair share of this queue in megabytes.    */
DECL|method|getSteadyFairShare ()
specifier|public
name|ResourceInfo
name|getSteadyFairShare
parameter_list|()
block|{
return|return
name|steadyFairResources
return|;
block|}
comment|/**    * Returns the fair share of this queue in megabytes    */
DECL|method|getFairShare ()
specifier|public
name|ResourceInfo
name|getFairShare
parameter_list|()
block|{
return|return
name|fairResources
return|;
block|}
DECL|method|getMinResources ()
specifier|public
name|ResourceInfo
name|getMinResources
parameter_list|()
block|{
return|return
name|minResources
return|;
block|}
DECL|method|getMaxResources ()
specifier|public
name|ResourceInfo
name|getMaxResources
parameter_list|()
block|{
return|return
name|maxResources
return|;
block|}
DECL|method|getMaxContainerAllocation ()
specifier|public
name|ResourceInfo
name|getMaxContainerAllocation
parameter_list|()
block|{
return|return
name|maxContainerAllocation
return|;
block|}
DECL|method|getReservedResources ()
specifier|public
name|ResourceInfo
name|getReservedResources
parameter_list|()
block|{
return|return
name|reservedResources
return|;
block|}
DECL|method|getMaxApplications ()
specifier|public
name|int
name|getMaxApplications
parameter_list|()
block|{
return|return
name|maxApps
return|;
block|}
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|queueName
return|;
block|}
DECL|method|getUsedResources ()
specifier|public
name|ResourceInfo
name|getUsedResources
parameter_list|()
block|{
return|return
name|usedResources
return|;
block|}
comment|/**    * @return the am used resource of this queue.    */
DECL|method|getAMUsedResources ()
specifier|public
name|ResourceInfo
name|getAMUsedResources
parameter_list|()
block|{
return|return
name|amUsedResources
return|;
block|}
comment|/**    * @return the am max resource of this queue.    */
DECL|method|getAMMaxResources ()
specifier|public
name|ResourceInfo
name|getAMMaxResources
parameter_list|()
block|{
return|return
name|amMaxResources
return|;
block|}
comment|/**    * @return the demand resource of this queue.      */
DECL|method|getDemandResources ()
specifier|public
name|ResourceInfo
name|getDemandResources
parameter_list|()
block|{
return|return
name|demandResources
return|;
block|}
comment|/**    * Returns the memory used by this queue as a fraction of the entire     * cluster capacity.    */
DECL|method|getUsedMemoryFraction ()
specifier|public
name|float
name|getUsedMemoryFraction
parameter_list|()
block|{
return|return
name|fractionMemUsed
return|;
block|}
comment|/**    * Returns the capacity of this queue as a fraction of the entire cluster     * capacity.    */
DECL|method|getMaxResourcesFraction ()
specifier|public
name|float
name|getMaxResourcesFraction
parameter_list|()
block|{
return|return
name|fractionMemMaxShare
return|;
block|}
comment|/**    * Returns the name of the scheduling policy used by this queue.    */
DECL|method|getSchedulingPolicy ()
specifier|public
name|String
name|getSchedulingPolicy
parameter_list|()
block|{
return|return
name|schedulingPolicy
return|;
block|}
DECL|method|getChildQueues ()
specifier|public
name|Collection
argument_list|<
name|FairSchedulerQueueInfo
argument_list|>
name|getChildQueues
parameter_list|()
block|{
return|return
name|childQueues
operator|!=
literal|null
condition|?
name|childQueues
operator|.
name|getQueueInfoList
argument_list|()
else|:
operator|new
name|ArrayList
argument_list|<
name|FairSchedulerQueueInfo
argument_list|>
argument_list|()
return|;
block|}
DECL|method|isPreemptable ()
specifier|public
name|boolean
name|isPreemptable
parameter_list|()
block|{
return|return
name|preemptable
return|;
block|}
block|}
end_class

end_unit

