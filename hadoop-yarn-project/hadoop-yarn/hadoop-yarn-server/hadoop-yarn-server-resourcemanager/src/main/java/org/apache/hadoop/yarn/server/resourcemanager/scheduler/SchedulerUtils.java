begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|scheduler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|api
operator|.
name|records
operator|.
name|ContainerExitStatus
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
name|ContainerState
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
name|api
operator|.
name|records
operator|.
name|ResourceRequest
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
name|exceptions
operator|.
name|InvalidResourceRequestException
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|rmnode
operator|.
name|RMNode
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
name|ResourceCalculator
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

begin_comment
comment|/**  * Utilities shared by schedulers.   */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SchedulerUtils
specifier|public
class|class
name|SchedulerUtils
block|{
DECL|field|recordFactory
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|RELEASED_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|RELEASED_CONTAINER
init|=
literal|"Container released by application"
decl_stmt|;
DECL|field|LOST_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|LOST_CONTAINER
init|=
literal|"Container released on a *lost* node"
decl_stmt|;
DECL|field|PREEMPTED_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|PREEMPTED_CONTAINER
init|=
literal|"Container preempted by scheduler"
decl_stmt|;
DECL|field|COMPLETED_APPLICATION
specifier|public
specifier|static
specifier|final
name|String
name|COMPLETED_APPLICATION
init|=
literal|"Container of a completed application"
decl_stmt|;
DECL|field|EXPIRED_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|EXPIRED_CONTAINER
init|=
literal|"Container expired since it was unused"
decl_stmt|;
DECL|field|UNRESERVED_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|UNRESERVED_CONTAINER
init|=
literal|"Container reservation no longer required."
decl_stmt|;
comment|/**    * Utility to create a {@link ContainerStatus} during exceptional    * circumstances.    *    * @param containerId {@link ContainerId} of returned/released/lost container.    * @param diagnostics diagnostic message    * @return<code>ContainerStatus</code> for an returned/released/lost     *         container    */
DECL|method|createAbnormalContainerStatus ( ContainerId containerId, String diagnostics)
specifier|public
specifier|static
name|ContainerStatus
name|createAbnormalContainerStatus
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|String
name|diagnostics
parameter_list|)
block|{
return|return
name|createAbnormalContainerStatus
argument_list|(
name|containerId
argument_list|,
name|ContainerExitStatus
operator|.
name|ABORTED
argument_list|,
name|diagnostics
argument_list|)
return|;
block|}
comment|/**    * Utility to create a {@link ContainerStatus} during exceptional    * circumstances.    *    * @param containerId {@link ContainerId} of returned/released/lost container.    * @param diagnostics diagnostic message    * @return<code>ContainerStatus</code> for an returned/released/lost    *         container    */
DECL|method|createPreemptedContainerStatus ( ContainerId containerId, String diagnostics)
specifier|public
specifier|static
name|ContainerStatus
name|createPreemptedContainerStatus
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|String
name|diagnostics
parameter_list|)
block|{
return|return
name|createAbnormalContainerStatus
argument_list|(
name|containerId
argument_list|,
name|ContainerExitStatus
operator|.
name|PREEMPTED
argument_list|,
name|diagnostics
argument_list|)
return|;
block|}
comment|/**    * Utility to create a {@link ContainerStatus} during exceptional    * circumstances.    *     * @param containerId {@link ContainerId} of returned/released/lost container.    * @param diagnostics diagnostic message    * @return<code>ContainerStatus</code> for an returned/released/lost     *         container    */
DECL|method|createAbnormalContainerStatus ( ContainerId containerId, int exitStatus, String diagnostics)
specifier|private
specifier|static
name|ContainerStatus
name|createAbnormalContainerStatus
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|int
name|exitStatus
parameter_list|,
name|String
name|diagnostics
parameter_list|)
block|{
name|ContainerStatus
name|containerStatus
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerStatus
operator|.
name|setContainerId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|containerStatus
operator|.
name|setDiagnostics
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
name|containerStatus
operator|.
name|setExitStatus
argument_list|(
name|exitStatus
argument_list|)
expr_stmt|;
name|containerStatus
operator|.
name|setState
argument_list|(
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
expr_stmt|;
return|return
name|containerStatus
return|;
block|}
comment|/**    * Utility method to normalize a list of resource requests, by insuring that    * the memory for each request is a multiple of minMemory and is not zero.    */
DECL|method|normalizeRequests ( List<ResourceRequest> asks, ResourceCalculator resourceCalculator, Resource clusterResource, Resource minimumResource, Resource maximumResource)
specifier|public
specifier|static
name|void
name|normalizeRequests
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|asks
parameter_list|,
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|minimumResource
parameter_list|,
name|Resource
name|maximumResource
parameter_list|)
block|{
for|for
control|(
name|ResourceRequest
name|ask
range|:
name|asks
control|)
block|{
name|normalizeRequest
argument_list|(
name|ask
argument_list|,
name|resourceCalculator
argument_list|,
name|clusterResource
argument_list|,
name|minimumResource
argument_list|,
name|maximumResource
argument_list|,
name|minimumResource
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Utility method to normalize a resource request, by insuring that the    * requested memory is a multiple of minMemory and is not zero.    */
DECL|method|normalizeRequest ( ResourceRequest ask, ResourceCalculator resourceCalculator, Resource clusterResource, Resource minimumResource, Resource maximumResource)
specifier|public
specifier|static
name|void
name|normalizeRequest
parameter_list|(
name|ResourceRequest
name|ask
parameter_list|,
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|minimumResource
parameter_list|,
name|Resource
name|maximumResource
parameter_list|)
block|{
name|Resource
name|normalized
init|=
name|Resources
operator|.
name|normalize
argument_list|(
name|resourceCalculator
argument_list|,
name|ask
operator|.
name|getCapability
argument_list|()
argument_list|,
name|minimumResource
argument_list|,
name|maximumResource
argument_list|,
name|minimumResource
argument_list|)
decl_stmt|;
name|ask
operator|.
name|setCapability
argument_list|(
name|normalized
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility method to normalize a list of resource requests, by insuring that    * the memory for each request is a multiple of minMemory and is not zero.    */
DECL|method|normalizeRequests ( List<ResourceRequest> asks, ResourceCalculator resourceCalculator, Resource clusterResource, Resource minimumResource, Resource maximumResource, Resource incrementResource)
specifier|public
specifier|static
name|void
name|normalizeRequests
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|asks
parameter_list|,
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|minimumResource
parameter_list|,
name|Resource
name|maximumResource
parameter_list|,
name|Resource
name|incrementResource
parameter_list|)
block|{
for|for
control|(
name|ResourceRequest
name|ask
range|:
name|asks
control|)
block|{
name|normalizeRequest
argument_list|(
name|ask
argument_list|,
name|resourceCalculator
argument_list|,
name|clusterResource
argument_list|,
name|minimumResource
argument_list|,
name|maximumResource
argument_list|,
name|incrementResource
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Utility method to normalize a resource request, by insuring that the    * requested memory is a multiple of minMemory and is not zero.    */
DECL|method|normalizeRequest ( ResourceRequest ask, ResourceCalculator resourceCalculator, Resource clusterResource, Resource minimumResource, Resource maximumResource, Resource incrementResource)
specifier|public
specifier|static
name|void
name|normalizeRequest
parameter_list|(
name|ResourceRequest
name|ask
parameter_list|,
name|ResourceCalculator
name|resourceCalculator
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|minimumResource
parameter_list|,
name|Resource
name|maximumResource
parameter_list|,
name|Resource
name|incrementResource
parameter_list|)
block|{
name|Resource
name|normalized
init|=
name|Resources
operator|.
name|normalize
argument_list|(
name|resourceCalculator
argument_list|,
name|ask
operator|.
name|getCapability
argument_list|()
argument_list|,
name|minimumResource
argument_list|,
name|maximumResource
argument_list|,
name|incrementResource
argument_list|)
decl_stmt|;
name|ask
operator|.
name|setCapability
argument_list|(
name|normalized
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility method to validate a resource request, by insuring that the    * requested memory/vcore is non-negative and not greater than max    *     * @throws<code>InvalidResourceRequestException</code> when there is invalid    *         request    */
DECL|method|validateResourceRequest (ResourceRequest resReq, Resource maximumResource)
specifier|public
specifier|static
name|void
name|validateResourceRequest
parameter_list|(
name|ResourceRequest
name|resReq
parameter_list|,
name|Resource
name|maximumResource
parameter_list|)
throws|throws
name|InvalidResourceRequestException
block|{
if|if
condition|(
name|resReq
operator|.
name|getCapability
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|<
literal|0
operator|||
name|resReq
operator|.
name|getCapability
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|>
name|maximumResource
operator|.
name|getMemory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidResourceRequestException
argument_list|(
literal|"Invalid resource request"
operator|+
literal|", requested memory< 0"
operator|+
literal|", or requested memory> max configured"
operator|+
literal|", requestedMemory="
operator|+
name|resReq
operator|.
name|getCapability
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|+
literal|", maxMemory="
operator|+
name|maximumResource
operator|.
name|getMemory
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|resReq
operator|.
name|getCapability
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
operator|<
literal|0
operator|||
name|resReq
operator|.
name|getCapability
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
operator|>
name|maximumResource
operator|.
name|getVirtualCores
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidResourceRequestException
argument_list|(
literal|"Invalid resource request"
operator|+
literal|", requested virtual cores< 0"
operator|+
literal|", or requested virtual cores> max configured"
operator|+
literal|", requestedVirtualCores="
operator|+
name|resReq
operator|.
name|getCapability
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
operator|+
literal|", maxVirtualCores="
operator|+
name|maximumResource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

