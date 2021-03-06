begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.scheduler
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
name|scheduler
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
name|ResourceUtilization
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
name|monitor
operator|.
name|ContainersMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * An implementation of the {@link ResourceUtilizationTracker} that equates  * resource utilization with the total resource allocated to the container.  */
end_comment

begin_class
DECL|class|AllocationBasedResourceUtilizationTracker
specifier|public
class|class
name|AllocationBasedResourceUtilizationTracker
implements|implements
name|ResourceUtilizationTracker
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AllocationBasedResourceUtilizationTracker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containersAllocation
specifier|private
name|ResourceUtilization
name|containersAllocation
decl_stmt|;
DECL|field|scheduler
specifier|private
name|ContainerScheduler
name|scheduler
decl_stmt|;
DECL|method|AllocationBasedResourceUtilizationTracker (ContainerScheduler scheduler)
name|AllocationBasedResourceUtilizationTracker
parameter_list|(
name|ContainerScheduler
name|scheduler
parameter_list|)
block|{
name|this
operator|.
name|containersAllocation
operator|=
name|ResourceUtilization
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
block|}
comment|/**    * Get the accumulation of totally allocated resources to a container.    * @return ResourceUtilization Resource Utilization.    */
annotation|@
name|Override
DECL|method|getCurrentUtilization ()
specifier|public
name|ResourceUtilization
name|getCurrentUtilization
parameter_list|()
block|{
return|return
name|this
operator|.
name|containersAllocation
return|;
block|}
comment|/**    * Add Container's resources to the accumulated Utilization.    * @param container Container.    */
annotation|@
name|Override
DECL|method|addContainerResources (Container container)
specifier|public
name|void
name|addContainerResources
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|ContainersMonitor
operator|.
name|increaseResourceUtilization
argument_list|(
name|getContainersMonitor
argument_list|()
argument_list|,
name|this
operator|.
name|containersAllocation
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Subtract Container's resources to the accumulated Utilization.    * @param container Container.    */
annotation|@
name|Override
DECL|method|subtractContainerResource (Container container)
specifier|public
name|void
name|subtractContainerResource
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|ContainersMonitor
operator|.
name|decreaseResourceUtilization
argument_list|(
name|getContainersMonitor
argument_list|()
argument_list|,
name|this
operator|.
name|containersAllocation
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check if NM has resources available currently to run the container.    * @param container Container.    * @return True, if NM has resources available currently to run the container.    */
annotation|@
name|Override
DECL|method|hasResourcesAvailable (Container container)
specifier|public
name|boolean
name|hasResourcesAvailable
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|long
name|pMemBytes
init|=
name|container
operator|.
name|getResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|*
literal|1024
operator|*
literal|1024L
decl_stmt|;
return|return
name|hasResourcesAvailable
argument_list|(
name|pMemBytes
argument_list|,
call|(
name|long
call|)
argument_list|(
name|getContainersMonitor
argument_list|()
operator|.
name|getVmemRatio
argument_list|()
operator|*
name|pMemBytes
argument_list|)
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
return|;
block|}
DECL|method|hasResourcesAvailable (long pMemBytes, long vMemBytes, int cpuVcores)
specifier|private
name|boolean
name|hasResourcesAvailable
parameter_list|(
name|long
name|pMemBytes
parameter_list|,
name|long
name|vMemBytes
parameter_list|,
name|int
name|cpuVcores
parameter_list|)
block|{
comment|// Check physical memory.
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"pMemCheck [current={} + asked={}> allowed={}]"
argument_list|,
name|this
operator|.
name|containersAllocation
operator|.
name|getPhysicalMemory
argument_list|()
argument_list|,
operator|(
name|pMemBytes
operator|>>
literal|20
operator|)
argument_list|,
operator|(
name|getContainersMonitor
argument_list|()
operator|.
name|getPmemAllocatedForContainers
argument_list|()
operator|>>
literal|20
operator|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|containersAllocation
operator|.
name|getPhysicalMemory
argument_list|()
operator|+
call|(
name|int
call|)
argument_list|(
name|pMemBytes
operator|>>
literal|20
argument_list|)
operator|>
call|(
name|int
call|)
argument_list|(
name|getContainersMonitor
argument_list|()
operator|.
name|getPmemAllocatedForContainers
argument_list|()
operator|>>
literal|20
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"before vMemCheck"
operator|+
literal|"[isEnabled={}, current={} + asked={}> allowed={}]"
argument_list|,
name|getContainersMonitor
argument_list|()
operator|.
name|isVmemCheckEnabled
argument_list|()
argument_list|,
name|this
operator|.
name|containersAllocation
operator|.
name|getVirtualMemory
argument_list|()
argument_list|,
operator|(
name|vMemBytes
operator|>>
literal|20
operator|)
argument_list|,
operator|(
name|getContainersMonitor
argument_list|()
operator|.
name|getVmemAllocatedForContainers
argument_list|()
operator|>>
literal|20
operator|)
argument_list|)
expr_stmt|;
block|}
comment|// Check virtual memory.
if|if
condition|(
name|getContainersMonitor
argument_list|()
operator|.
name|isVmemCheckEnabled
argument_list|()
operator|&&
name|this
operator|.
name|containersAllocation
operator|.
name|getVirtualMemory
argument_list|()
operator|+
call|(
name|int
call|)
argument_list|(
name|vMemBytes
operator|>>
literal|20
argument_list|)
operator|>
call|(
name|int
call|)
argument_list|(
name|getContainersMonitor
argument_list|()
operator|.
name|getVmemAllocatedForContainers
argument_list|()
operator|>>
literal|20
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"before cpuCheck [asked={}> allowed={}]"
argument_list|,
name|this
operator|.
name|containersAllocation
operator|.
name|getCPU
argument_list|()
argument_list|,
name|getContainersMonitor
argument_list|()
operator|.
name|getVCoresAllocatedForContainers
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check CPU.
if|if
condition|(
name|this
operator|.
name|containersAllocation
operator|.
name|getCPU
argument_list|()
operator|+
name|cpuVcores
operator|>
name|getContainersMonitor
argument_list|()
operator|.
name|getVCoresAllocatedForContainers
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|getContainersMonitor ()
specifier|public
name|ContainersMonitor
name|getContainersMonitor
parameter_list|()
block|{
return|return
name|this
operator|.
name|scheduler
operator|.
name|getContainersMonitor
argument_list|()
return|;
block|}
block|}
end_class

end_unit

