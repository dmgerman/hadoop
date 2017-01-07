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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|conf
operator|.
name|Configuration
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
name|QueueACL
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
name|QueueInfo
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
name|conf
operator|.
name|YarnConfiguration
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
name|InvalidLabelResourceRequestException
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
name|security
operator|.
name|AccessType
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
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|rmcontainer
operator|.
name|RMContainer
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
name|rmcontainer
operator|.
name|RMContainerImpl
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
name|SchedulingMode
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
name|scheduler
operator|.
name|SchedulerRequestKey
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
DECL|field|UPDATED_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|UPDATED_CONTAINER
init|=
literal|"Temporary container killed by application for ExeutionType update"
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
comment|/**    * Utility method to normalize a resource request, by insuring that the    * requested memory is a multiple of minMemory and is not zero.    */
annotation|@
name|VisibleForTesting
DECL|method|normalizeRequest ( ResourceRequest ask, ResourceCalculator resourceCalculator, Resource minimumResource, Resource maximumResource)
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
name|minimumResource
parameter_list|,
name|Resource
name|maximumResource
parameter_list|)
block|{
name|ask
operator|.
name|setCapability
argument_list|(
name|getNormalizedResource
argument_list|(
name|ask
operator|.
name|getCapability
argument_list|()
argument_list|,
name|resourceCalculator
argument_list|,
name|minimumResource
argument_list|,
name|maximumResource
argument_list|,
name|minimumResource
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility method to normalize a resource request, by insuring that the    * requested memory is a multiple of increment resource and is not zero.    *    * @return normalized resource    */
DECL|method|getNormalizedResource ( Resource ask, ResourceCalculator resourceCalculator, Resource minimumResource, Resource maximumResource, Resource incrementResource)
specifier|public
specifier|static
name|Resource
name|getNormalizedResource
parameter_list|(
name|Resource
name|ask
parameter_list|,
name|ResourceCalculator
name|resourceCalculator
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
argument_list|,
name|minimumResource
argument_list|,
name|maximumResource
argument_list|,
name|incrementResource
argument_list|)
decl_stmt|;
return|return
name|normalized
return|;
block|}
DECL|method|normalizeNodeLabelExpressionInRequest ( ResourceRequest resReq, QueueInfo queueInfo)
specifier|private
specifier|static
name|void
name|normalizeNodeLabelExpressionInRequest
parameter_list|(
name|ResourceRequest
name|resReq
parameter_list|,
name|QueueInfo
name|queueInfo
parameter_list|)
block|{
name|String
name|labelExp
init|=
name|resReq
operator|.
name|getNodeLabelExpression
argument_list|()
decl_stmt|;
comment|// if queue has default label expression, and RR doesn't have, use the
comment|// default label expression of queue
if|if
condition|(
name|labelExp
operator|==
literal|null
operator|&&
name|queueInfo
operator|!=
literal|null
operator|&&
name|ResourceRequest
operator|.
name|ANY
operator|.
name|equals
argument_list|(
name|resReq
operator|.
name|getResourceName
argument_list|()
argument_list|)
condition|)
block|{
name|labelExp
operator|=
name|queueInfo
operator|.
name|getDefaultNodeLabelExpression
argument_list|()
expr_stmt|;
block|}
comment|// If labelExp still equals to null, set it to be NO_LABEL
if|if
condition|(
name|labelExp
operator|==
literal|null
condition|)
block|{
name|labelExp
operator|=
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
expr_stmt|;
block|}
name|resReq
operator|.
name|setNodeLabelExpression
argument_list|(
name|labelExp
argument_list|)
expr_stmt|;
block|}
DECL|method|normalizeAndValidateRequest (ResourceRequest resReq, Resource maximumResource, String queueName, YarnScheduler scheduler, boolean isRecovery, RMContext rmContext)
specifier|public
specifier|static
name|void
name|normalizeAndValidateRequest
parameter_list|(
name|ResourceRequest
name|resReq
parameter_list|,
name|Resource
name|maximumResource
parameter_list|,
name|String
name|queueName
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|,
name|boolean
name|isRecovery
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|InvalidResourceRequestException
block|{
name|normalizeAndValidateRequest
argument_list|(
name|resReq
argument_list|,
name|maximumResource
argument_list|,
name|queueName
argument_list|,
name|scheduler
argument_list|,
name|isRecovery
argument_list|,
name|rmContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|normalizeAndValidateRequest (ResourceRequest resReq, Resource maximumResource, String queueName, YarnScheduler scheduler, boolean isRecovery, RMContext rmContext, QueueInfo queueInfo)
specifier|public
specifier|static
name|void
name|normalizeAndValidateRequest
parameter_list|(
name|ResourceRequest
name|resReq
parameter_list|,
name|Resource
name|maximumResource
parameter_list|,
name|String
name|queueName
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|,
name|boolean
name|isRecovery
parameter_list|,
name|RMContext
name|rmContext
parameter_list|,
name|QueueInfo
name|queueInfo
parameter_list|)
throws|throws
name|InvalidResourceRequestException
block|{
name|Configuration
name|conf
init|=
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
decl_stmt|;
comment|// If Node label is not enabled throw exception
if|if
condition|(
literal|null
operator|!=
name|conf
operator|&&
operator|!
name|YarnConfiguration
operator|.
name|areNodeLabelsEnabled
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|String
name|labelExp
init|=
name|resReq
operator|.
name|getNodeLabelExpression
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
operator|.
name|equals
argument_list|(
name|labelExp
argument_list|)
operator|||
literal|null
operator|==
name|labelExp
operator|)
condition|)
block|{
throw|throw
operator|new
name|InvalidLabelResourceRequestException
argument_list|(
literal|"Invalid resource request, node label not enabled "
operator|+
literal|"but request contains label expression"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
literal|null
operator|==
name|queueInfo
condition|)
block|{
try|try
block|{
name|queueInfo
operator|=
name|scheduler
operator|.
name|getQueueInfo
argument_list|(
name|queueName
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// it is possible queue cannot get when queue mapping is set, just ignore
comment|// the queueInfo here, and move forward
block|}
block|}
name|SchedulerUtils
operator|.
name|normalizeNodeLabelExpressionInRequest
argument_list|(
name|resReq
argument_list|,
name|queueInfo
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isRecovery
condition|)
block|{
name|validateResourceRequest
argument_list|(
name|resReq
argument_list|,
name|maximumResource
argument_list|,
name|queueInfo
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|normalizeAndvalidateRequest (ResourceRequest resReq, Resource maximumResource, String queueName, YarnScheduler scheduler, RMContext rmContext)
specifier|public
specifier|static
name|void
name|normalizeAndvalidateRequest
parameter_list|(
name|ResourceRequest
name|resReq
parameter_list|,
name|Resource
name|maximumResource
parameter_list|,
name|String
name|queueName
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|InvalidResourceRequestException
block|{
name|normalizeAndvalidateRequest
argument_list|(
name|resReq
argument_list|,
name|maximumResource
argument_list|,
name|queueName
argument_list|,
name|scheduler
argument_list|,
name|rmContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|normalizeAndvalidateRequest (ResourceRequest resReq, Resource maximumResource, String queueName, YarnScheduler scheduler, RMContext rmContext, QueueInfo queueInfo)
specifier|public
specifier|static
name|void
name|normalizeAndvalidateRequest
parameter_list|(
name|ResourceRequest
name|resReq
parameter_list|,
name|Resource
name|maximumResource
parameter_list|,
name|String
name|queueName
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|,
name|RMContext
name|rmContext
parameter_list|,
name|QueueInfo
name|queueInfo
parameter_list|)
throws|throws
name|InvalidResourceRequestException
block|{
name|normalizeAndValidateRequest
argument_list|(
name|resReq
argument_list|,
name|maximumResource
argument_list|,
name|queueName
argument_list|,
name|scheduler
argument_list|,
literal|false
argument_list|,
name|rmContext
argument_list|,
name|queueInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * Utility method to validate a resource request, by insuring that the    * requested memory/vcore is non-negative and not greater than max    *     * @throws InvalidResourceRequestException when there is invalid request    */
DECL|method|validateResourceRequest (ResourceRequest resReq, Resource maximumResource, QueueInfo queueInfo, RMContext rmContext)
specifier|private
specifier|static
name|void
name|validateResourceRequest
parameter_list|(
name|ResourceRequest
name|resReq
parameter_list|,
name|Resource
name|maximumResource
parameter_list|,
name|QueueInfo
name|queueInfo
parameter_list|,
name|RMContext
name|rmContext
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
name|getMemorySize
argument_list|()
operator|<
literal|0
operator|||
name|resReq
operator|.
name|getCapability
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|>
name|maximumResource
operator|.
name|getMemorySize
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
name|getMemorySize
argument_list|()
operator|+
literal|", maxMemory="
operator|+
name|maximumResource
operator|.
name|getMemorySize
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
name|String
name|labelExp
init|=
name|resReq
operator|.
name|getNodeLabelExpression
argument_list|()
decl_stmt|;
comment|// we don't allow specify label expression other than resourceName=ANY now
if|if
condition|(
operator|!
name|ResourceRequest
operator|.
name|ANY
operator|.
name|equals
argument_list|(
name|resReq
operator|.
name|getResourceName
argument_list|()
argument_list|)
operator|&&
name|labelExp
operator|!=
literal|null
operator|&&
operator|!
name|labelExp
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidLabelResourceRequestException
argument_list|(
literal|"Invalid resource request, queue="
operator|+
name|queueInfo
operator|.
name|getQueueName
argument_list|()
operator|+
literal|" specified node label expression in a "
operator|+
literal|"resource request has resource name = "
operator|+
name|resReq
operator|.
name|getResourceName
argument_list|()
argument_list|)
throw|;
block|}
comment|// we don't allow specify label expression with more than one node labels now
if|if
condition|(
name|labelExp
operator|!=
literal|null
operator|&&
name|labelExp
operator|.
name|contains
argument_list|(
literal|"&&"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidLabelResourceRequestException
argument_list|(
literal|"Invalid resource request, queue="
operator|+
name|queueInfo
operator|.
name|getQueueName
argument_list|()
operator|+
literal|" specified more than one node label "
operator|+
literal|"in a node label expression, node label expression = "
operator|+
name|labelExp
argument_list|)
throw|;
block|}
if|if
condition|(
name|labelExp
operator|!=
literal|null
operator|&&
operator|!
name|labelExp
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
name|queueInfo
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|checkQueueLabelExpression
argument_list|(
name|queueInfo
operator|.
name|getAccessibleNodeLabels
argument_list|()
argument_list|,
name|labelExp
argument_list|,
name|rmContext
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidLabelResourceRequestException
argument_list|(
literal|"Invalid resource request"
operator|+
literal|", queue="
operator|+
name|queueInfo
operator|.
name|getQueueName
argument_list|()
operator|+
literal|" doesn't have permission to access all labels "
operator|+
literal|"in resource request. labelExpression of resource request="
operator|+
name|labelExp
operator|+
literal|". Queue labels="
operator|+
operator|(
name|queueInfo
operator|.
name|getAccessibleNodeLabels
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|StringUtils
operator|.
name|join
argument_list|(
name|queueInfo
operator|.
name|getAccessibleNodeLabels
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
literal|','
argument_list|)
operator|)
argument_list|)
throw|;
block|}
else|else
block|{
name|checkQueueLabelInLabelManager
argument_list|(
name|labelExp
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|checkQueueLabelInLabelManager (String labelExpression, RMContext rmContext)
specifier|private
specifier|static
name|void
name|checkQueueLabelInLabelManager
parameter_list|(
name|String
name|labelExpression
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|InvalidLabelResourceRequestException
block|{
comment|// check node label manager contains this label
if|if
condition|(
literal|null
operator|!=
name|rmContext
condition|)
block|{
name|RMNodeLabelsManager
name|nlm
init|=
name|rmContext
operator|.
name|getNodeLabelManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|nlm
operator|!=
literal|null
operator|&&
operator|!
name|nlm
operator|.
name|containsNodeLabel
argument_list|(
name|labelExpression
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidLabelResourceRequestException
argument_list|(
literal|"Invalid label resource request, cluster do not contain "
operator|+
literal|", label= "
operator|+
name|labelExpression
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Check queue label expression, check if node label in queue's    * node-label-expression existed in clusterNodeLabels if rmContext != null    */
DECL|method|checkQueueLabelExpression (Set<String> queueLabels, String labelExpression, RMContext rmContext)
specifier|public
specifier|static
name|boolean
name|checkQueueLabelExpression
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|queueLabels
parameter_list|,
name|String
name|labelExpression
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
block|{
comment|// if label expression is empty, we can allocate container on any node
if|if
condition|(
name|labelExpression
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|String
name|str
range|:
name|labelExpression
operator|.
name|split
argument_list|(
literal|"&&"
argument_list|)
control|)
block|{
name|str
operator|=
name|str
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|str
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// check queue label
if|if
condition|(
name|queueLabels
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|queueLabels
operator|.
name|contains
argument_list|(
name|str
argument_list|)
operator|&&
operator|!
name|queueLabels
operator|.
name|contains
argument_list|(
name|RMNodeLabelsManager
operator|.
name|ANY
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|toAccessType (QueueACL acl)
specifier|public
specifier|static
name|AccessType
name|toAccessType
parameter_list|(
name|QueueACL
name|acl
parameter_list|)
block|{
switch|switch
condition|(
name|acl
condition|)
block|{
case|case
name|ADMINISTER_QUEUE
case|:
return|return
name|AccessType
operator|.
name|ADMINISTER_QUEUE
return|;
case|case
name|SUBMIT_APPLICATIONS
case|:
return|return
name|AccessType
operator|.
name|SUBMIT_APP
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|checkResourceRequestMatchingNodePartition ( String requestedPartition, String nodePartition, SchedulingMode schedulingMode)
specifier|public
specifier|static
name|boolean
name|checkResourceRequestMatchingNodePartition
parameter_list|(
name|String
name|requestedPartition
parameter_list|,
name|String
name|nodePartition
parameter_list|,
name|SchedulingMode
name|schedulingMode
parameter_list|)
block|{
comment|// We will only look at node label = nodeLabelToLookAt according to
comment|// schedulingMode and partition of node.
name|String
name|nodePartitionToLookAt
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|schedulingMode
operator|==
name|SchedulingMode
operator|.
name|RESPECT_PARTITION_EXCLUSIVITY
condition|)
block|{
name|nodePartitionToLookAt
operator|=
name|nodePartition
expr_stmt|;
block|}
else|else
block|{
name|nodePartitionToLookAt
operator|=
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|requestedPartition
condition|)
block|{
name|requestedPartition
operator|=
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
expr_stmt|;
block|}
return|return
name|requestedPartition
operator|.
name|equals
argument_list|(
name|nodePartitionToLookAt
argument_list|)
return|;
block|}
DECL|method|hasPendingResourceRequest (ResourceCalculator rc, ResourceUsage usage, String partitionToLookAt, Resource cluster)
specifier|private
specifier|static
name|boolean
name|hasPendingResourceRequest
parameter_list|(
name|ResourceCalculator
name|rc
parameter_list|,
name|ResourceUsage
name|usage
parameter_list|,
name|String
name|partitionToLookAt
parameter_list|,
name|Resource
name|cluster
parameter_list|)
block|{
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|cluster
argument_list|,
name|usage
operator|.
name|getPending
argument_list|(
name|partitionToLookAt
argument_list|)
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Private
DECL|method|hasPendingResourceRequest (ResourceCalculator rc, ResourceUsage usage, String nodePartition, Resource cluster, SchedulingMode schedulingMode)
specifier|public
specifier|static
name|boolean
name|hasPendingResourceRequest
parameter_list|(
name|ResourceCalculator
name|rc
parameter_list|,
name|ResourceUsage
name|usage
parameter_list|,
name|String
name|nodePartition
parameter_list|,
name|Resource
name|cluster
parameter_list|,
name|SchedulingMode
name|schedulingMode
parameter_list|)
block|{
name|String
name|partitionToLookAt
init|=
name|nodePartition
decl_stmt|;
if|if
condition|(
name|schedulingMode
operator|==
name|SchedulingMode
operator|.
name|IGNORE_PARTITION_EXCLUSIVITY
condition|)
block|{
name|partitionToLookAt
operator|=
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
expr_stmt|;
block|}
return|return
name|hasPendingResourceRequest
argument_list|(
name|rc
argument_list|,
name|usage
argument_list|,
name|partitionToLookAt
argument_list|,
name|cluster
argument_list|)
return|;
block|}
DECL|method|createOpportunisticRmContainer (RMContext rmContext, Container container, boolean isRemotelyAllocated)
specifier|public
specifier|static
name|RMContainer
name|createOpportunisticRmContainer
parameter_list|(
name|RMContext
name|rmContext
parameter_list|,
name|Container
name|container
parameter_list|,
name|boolean
name|isRemotelyAllocated
parameter_list|)
block|{
name|SchedulerApplicationAttempt
name|appAttempt
init|=
operator|(
operator|(
name|AbstractYarnScheduler
operator|)
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|)
operator|.
name|getCurrentAttemptForContainer
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|RMContainer
name|rmContainer
init|=
operator|new
name|RMContainerImpl
argument_list|(
name|container
argument_list|,
name|SchedulerRequestKey
operator|.
name|extractFrom
argument_list|(
name|container
argument_list|)
argument_list|,
name|appAttempt
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getUser
argument_list|()
argument_list|,
name|rmContext
argument_list|,
name|isRemotelyAllocated
argument_list|)
decl_stmt|;
name|appAttempt
operator|.
name|addRMContainer
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|rmContainer
argument_list|)
expr_stmt|;
operator|(
operator|(
name|AbstractYarnScheduler
operator|)
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|)
operator|.
name|getNode
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|allocateContainer
argument_list|(
name|rmContainer
argument_list|)
expr_stmt|;
return|return
name|rmContainer
return|;
block|}
block|}
end_class

end_unit

