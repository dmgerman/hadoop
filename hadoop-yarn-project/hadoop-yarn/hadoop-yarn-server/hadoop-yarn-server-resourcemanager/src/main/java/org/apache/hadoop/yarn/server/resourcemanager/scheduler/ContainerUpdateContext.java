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
name|ApplicationAttemptId
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
name|ApplicationId
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
name|ContainerUpdateType
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
name|ExecutionType
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
name|ExecutionTypeRequest
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
name|NodeId
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
name|api
operator|.
name|records
operator|.
name|UpdateContainerRequest
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
name|placement
operator|.
name|AppPlacementAllocator
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
name|Resources
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Class encapsulates all outstanding container increase and decrease  * requests for an application.  */
end_comment

begin_class
DECL|class|ContainerUpdateContext
specifier|public
class|class
name|ContainerUpdateContext
block|{
DECL|field|UNDEFINED
specifier|public
specifier|static
specifier|final
name|ContainerId
name|UNDEFINED
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|RECORD_FACTORY
specifier|protected
specifier|static
specifier|final
name|RecordFactory
name|RECORD_FACTORY
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|// Keep track of containers that are undergoing promotion
specifier|private
specifier|final
name|Map
argument_list|<
name|SchedulerRequestKey
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
DECL|field|outstandingIncreases
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|outstandingIncreases
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|outstandingDecreases
specifier|private
specifier|final
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Resource
argument_list|>
name|outstandingDecreases
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|appSchedulingInfo
specifier|private
specifier|final
name|AppSchedulingInfo
name|appSchedulingInfo
decl_stmt|;
DECL|method|ContainerUpdateContext (AppSchedulingInfo appSchedulingInfo)
name|ContainerUpdateContext
parameter_list|(
name|AppSchedulingInfo
name|appSchedulingInfo
parameter_list|)
block|{
name|this
operator|.
name|appSchedulingInfo
operator|=
name|appSchedulingInfo
expr_stmt|;
block|}
comment|/**    * Add the container to outstanding decreases.    * @param updateReq UpdateContainerRequest.    * @param schedulerNode SchedulerNode.    * @param container Container.    * @return If it was possible to decrease the container.    */
DECL|method|checkAndAddToOutstandingDecreases ( UpdateContainerRequest updateReq, SchedulerNode schedulerNode, Container container)
specifier|public
specifier|synchronized
name|boolean
name|checkAndAddToOutstandingDecreases
parameter_list|(
name|UpdateContainerRequest
name|updateReq
parameter_list|,
name|SchedulerNode
name|schedulerNode
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
if|if
condition|(
name|outstandingDecreases
operator|.
name|containsKey
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|ContainerUpdateType
operator|.
name|DECREASE_RESOURCE
operator|==
name|updateReq
operator|.
name|getContainerUpdateType
argument_list|()
condition|)
block|{
name|SchedulerRequestKey
name|updateKey
init|=
operator|new
name|SchedulerRequestKey
argument_list|(
name|container
operator|.
name|getPriority
argument_list|()
argument_list|,
name|container
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|cancelPreviousRequest
argument_list|(
name|schedulerNode
argument_list|,
name|updateKey
argument_list|)
expr_stmt|;
name|outstandingDecreases
operator|.
name|put
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|updateReq
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|outstandingDecreases
operator|.
name|put
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Add the container to outstanding increases.    * @param rmContainer RMContainer.    * @param schedulerNode SchedulerNode.    * @param updateRequest UpdateContainerRequest.    * @return true if updated to outstanding increases was successful.    */
DECL|method|checkAndAddToOutstandingIncreases ( RMContainer rmContainer, SchedulerNode schedulerNode, UpdateContainerRequest updateRequest)
specifier|public
specifier|synchronized
name|boolean
name|checkAndAddToOutstandingIncreases
parameter_list|(
name|RMContainer
name|rmContainer
parameter_list|,
name|SchedulerNode
name|schedulerNode
parameter_list|,
name|UpdateContainerRequest
name|updateRequest
parameter_list|)
block|{
name|Container
name|container
init|=
name|rmContainer
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|SchedulerRequestKey
name|schedulerKey
init|=
name|SchedulerRequestKey
operator|.
name|create
argument_list|(
name|updateRequest
argument_list|,
name|rmContainer
operator|.
name|getAllocatedSchedulerKey
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Resource
argument_list|,
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
argument_list|>
argument_list|>
name|resourceMap
init|=
name|outstandingIncreases
operator|.
name|get
argument_list|(
name|schedulerKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|resourceMap
operator|==
literal|null
condition|)
block|{
name|resourceMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|outstandingIncreases
operator|.
name|put
argument_list|(
name|schedulerKey
argument_list|,
name|resourceMap
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Updating Resource for and existing increase container
if|if
condition|(
name|ContainerUpdateType
operator|.
name|INCREASE_RESOURCE
operator|==
name|updateRequest
operator|.
name|getContainerUpdateType
argument_list|()
condition|)
block|{
name|cancelPreviousRequest
argument_list|(
name|schedulerNode
argument_list|,
name|schedulerKey
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
name|Resource
name|resToIncrease
init|=
name|getResourceToIncrease
argument_list|(
name|updateRequest
argument_list|,
name|rmContainer
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
argument_list|>
name|locationMap
init|=
name|resourceMap
operator|.
name|get
argument_list|(
name|resToIncrease
argument_list|)
decl_stmt|;
if|if
condition|(
name|locationMap
operator|==
literal|null
condition|)
block|{
name|locationMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|resourceMap
operator|.
name|put
argument_list|(
name|resToIncrease
argument_list|,
name|locationMap
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
init|=
name|locationMap
operator|.
name|get
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerIds
operator|==
literal|null
condition|)
block|{
name|containerIds
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|locationMap
operator|.
name|put
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|containerIds
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|outstandingDecreases
operator|.
name|containsKey
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|containerIds
operator|.
name|add
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Resources
operator|.
name|isNone
argument_list|(
name|resToIncrease
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|SchedulerRequestKey
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
name|updateResReqs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|resMap
init|=
name|createResourceRequests
argument_list|(
name|rmContainer
argument_list|,
name|schedulerNode
argument_list|,
name|schedulerKey
argument_list|,
name|resToIncrease
argument_list|)
decl_stmt|;
name|updateResReqs
operator|.
name|put
argument_list|(
name|schedulerKey
argument_list|,
name|resMap
argument_list|)
expr_stmt|;
name|appSchedulingInfo
operator|.
name|addRequestToAppPlacement
argument_list|(
literal|false
argument_list|,
name|updateResReqs
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|cancelPreviousRequest (SchedulerNode schedulerNode, SchedulerRequestKey schedulerKey)
specifier|private
name|void
name|cancelPreviousRequest
parameter_list|(
name|SchedulerNode
name|schedulerNode
parameter_list|,
name|SchedulerRequestKey
name|schedulerKey
parameter_list|)
block|{
name|AppPlacementAllocator
argument_list|<
name|SchedulerNode
argument_list|>
name|appPlacementAllocator
init|=
name|appSchedulingInfo
operator|.
name|getAppPlacementAllocator
argument_list|(
name|schedulerKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|appPlacementAllocator
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|resourceRequests
init|=
name|appPlacementAllocator
operator|.
name|getResourceRequests
argument_list|()
decl_stmt|;
name|ResourceRequest
name|prevReq
init|=
name|resourceRequests
operator|.
name|get
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
decl_stmt|;
comment|// Decrement the pending using a dummy RR with
comment|// resource = prev update req capability
if|if
condition|(
name|prevReq
operator|!=
literal|null
condition|)
block|{
name|appSchedulingInfo
operator|.
name|allocate
argument_list|(
name|NodeType
operator|.
name|OFF_SWITCH
argument_list|,
name|schedulerNode
argument_list|,
name|schedulerKey
argument_list|,
name|Container
operator|.
name|newInstance
argument_list|(
name|UNDEFINED
argument_list|,
name|schedulerNode
operator|.
name|getNodeID
argument_list|()
argument_list|,
literal|"host:port"
argument_list|,
name|prevReq
operator|.
name|getCapability
argument_list|()
argument_list|,
name|schedulerKey
operator|.
name|getPriority
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createResourceRequests ( RMContainer rmContainer, SchedulerNode schedulerNode, SchedulerRequestKey schedulerKey, Resource resToIncrease)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|createResourceRequests
parameter_list|(
name|RMContainer
name|rmContainer
parameter_list|,
name|SchedulerNode
name|schedulerNode
parameter_list|,
name|SchedulerRequestKey
name|schedulerKey
parameter_list|,
name|Resource
name|resToIncrease
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|resMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|resMap
operator|.
name|put
argument_list|(
name|rmContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
argument_list|,
name|createResourceReqForIncrease
argument_list|(
name|schedulerKey
argument_list|,
name|resToIncrease
argument_list|,
name|RECORD_FACTORY
operator|.
name|newRecordInstance
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
argument_list|,
name|rmContainer
argument_list|,
name|rmContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|resMap
operator|.
name|put
argument_list|(
name|schedulerNode
operator|.
name|getRackName
argument_list|()
argument_list|,
name|createResourceReqForIncrease
argument_list|(
name|schedulerKey
argument_list|,
name|resToIncrease
argument_list|,
name|RECORD_FACTORY
operator|.
name|newRecordInstance
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
argument_list|,
name|rmContainer
argument_list|,
name|schedulerNode
operator|.
name|getRackName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|resMap
operator|.
name|put
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|createResourceReqForIncrease
argument_list|(
name|schedulerKey
argument_list|,
name|resToIncrease
argument_list|,
name|RECORD_FACTORY
operator|.
name|newRecordInstance
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
argument_list|,
name|rmContainer
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|resMap
return|;
block|}
DECL|method|getResourceToIncrease (UpdateContainerRequest updateReq, RMContainer rmContainer)
specifier|private
name|Resource
name|getResourceToIncrease
parameter_list|(
name|UpdateContainerRequest
name|updateReq
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|)
block|{
if|if
condition|(
name|updateReq
operator|.
name|getContainerUpdateType
argument_list|()
operator|==
name|ContainerUpdateType
operator|.
name|PROMOTE_EXECUTION_TYPE
condition|)
block|{
return|return
name|rmContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
return|;
block|}
if|if
condition|(
name|updateReq
operator|.
name|getContainerUpdateType
argument_list|()
operator|==
name|ContainerUpdateType
operator|.
name|INCREASE_RESOURCE
condition|)
block|{
comment|//       This has to equal the Resources in excess of fitsIn()
comment|//       for container increase and is equal to the container total
comment|//       resource for Promotion.
name|Resource
name|maxCap
init|=
name|Resources
operator|.
name|componentwiseMax
argument_list|(
name|updateReq
operator|.
name|getCapability
argument_list|()
argument_list|,
name|rmContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Resources
operator|.
name|add
argument_list|(
name|maxCap
argument_list|,
name|Resources
operator|.
name|negate
argument_list|(
name|rmContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|createResourceReqForIncrease ( SchedulerRequestKey schedulerRequestKey, Resource resToIncrease, ResourceRequest rr, RMContainer rmContainer, String resourceName)
specifier|private
specifier|static
name|ResourceRequest
name|createResourceReqForIncrease
parameter_list|(
name|SchedulerRequestKey
name|schedulerRequestKey
parameter_list|,
name|Resource
name|resToIncrease
parameter_list|,
name|ResourceRequest
name|rr
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|,
name|String
name|resourceName
parameter_list|)
block|{
name|rr
operator|.
name|setResourceName
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setNumContainers
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setRelaxLocality
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setPriority
argument_list|(
name|rmContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setAllocationRequestId
argument_list|(
name|schedulerRequestKey
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setCapability
argument_list|(
name|resToIncrease
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setNodeLabelExpression
argument_list|(
name|rmContainer
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setExecutionTypeRequest
argument_list|(
name|ExecutionTypeRequest
operator|.
name|newInstance
argument_list|(
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|rr
return|;
block|}
comment|/**    * Remove Container from outstanding increases / decreases. Calling this    * method essentially completes the update process.    * @param schedulerKey SchedulerRequestKey.    * @param container Container.    */
DECL|method|removeFromOutstandingUpdate ( SchedulerRequestKey schedulerKey, Container container)
specifier|public
specifier|synchronized
name|void
name|removeFromOutstandingUpdate
parameter_list|(
name|SchedulerRequestKey
name|schedulerKey
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
name|Map
argument_list|<
name|Resource
argument_list|,
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
argument_list|>
argument_list|>
name|resourceMap
init|=
name|outstandingIncreases
operator|.
name|get
argument_list|(
name|schedulerKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|resourceMap
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
argument_list|>
name|locationMap
init|=
name|resourceMap
operator|.
name|get
argument_list|(
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|locationMap
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
init|=
name|locationMap
operator|.
name|get
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerIds
operator|!=
literal|null
operator|&&
operator|!
name|containerIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|containerIds
operator|.
name|remove
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|containerIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|locationMap
operator|.
name|remove
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|locationMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|resourceMap
operator|.
name|remove
argument_list|(
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|resourceMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|outstandingIncreases
operator|.
name|remove
argument_list|(
name|schedulerKey
argument_list|)
expr_stmt|;
block|}
block|}
name|outstandingDecreases
operator|.
name|remove
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check if a new container is to be matched up against an outstanding    * Container increase request.    * @param node SchedulerNode.    * @param schedulerKey SchedulerRequestKey.    * @param rmContainer RMContainer.    * @return ContainerId.    */
DECL|method|matchContainerToOutstandingIncreaseReq ( SchedulerNode node, SchedulerRequestKey schedulerKey, RMContainer rmContainer)
specifier|public
name|ContainerId
name|matchContainerToOutstandingIncreaseReq
parameter_list|(
name|SchedulerNode
name|node
parameter_list|,
name|SchedulerRequestKey
name|schedulerKey
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|)
block|{
name|ContainerId
name|retVal
init|=
literal|null
decl_stmt|;
name|Container
name|container
init|=
name|rmContainer
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Resource
argument_list|,
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
argument_list|>
argument_list|>
name|resourceMap
init|=
name|outstandingIncreases
operator|.
name|get
argument_list|(
name|schedulerKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|resourceMap
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
argument_list|>
name|locationMap
init|=
name|resourceMap
operator|.
name|get
argument_list|(
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|locationMap
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
init|=
name|locationMap
operator|.
name|get
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerIds
operator|!=
literal|null
operator|&&
operator|!
name|containerIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|retVal
operator|=
name|containerIds
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Allocation happened on NM on the same host, but not on the NM
comment|// we need.. We need to signal that this container has to be released.
comment|// We also need to add these requests back.. to be reallocated.
if|if
condition|(
name|resourceMap
operator|!=
literal|null
operator|&&
name|retVal
operator|==
literal|null
condition|)
block|{
name|Map
argument_list|<
name|SchedulerRequestKey
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
name|reqsToUpdate
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|resMap
init|=
name|createResourceRequests
argument_list|(
name|rmContainer
argument_list|,
name|node
argument_list|,
name|schedulerKey
argument_list|,
name|rmContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
argument_list|)
decl_stmt|;
name|reqsToUpdate
operator|.
name|put
argument_list|(
name|schedulerKey
argument_list|,
name|resMap
argument_list|)
expr_stmt|;
name|appSchedulingInfo
operator|.
name|addRequestToAppPlacement
argument_list|(
literal|true
argument_list|,
name|reqsToUpdate
argument_list|)
expr_stmt|;
return|return
name|UNDEFINED
return|;
block|}
return|return
name|retVal
return|;
block|}
comment|/**    * Swaps the existing RMContainer's and the temp RMContainers internal    * container references after adjusting the resources in each.    * @param tempRMContainer Temp RMContainer.    * @param existingRMContainer Existing RMContainer.    * @param updateType Update Type.    * @return Existing RMContainer after swapping the container references.    */
DECL|method|swapContainer (RMContainer tempRMContainer, RMContainer existingRMContainer, ContainerUpdateType updateType)
specifier|public
name|RMContainer
name|swapContainer
parameter_list|(
name|RMContainer
name|tempRMContainer
parameter_list|,
name|RMContainer
name|existingRMContainer
parameter_list|,
name|ContainerUpdateType
name|updateType
parameter_list|)
block|{
name|ContainerId
name|matchedContainerId
init|=
name|existingRMContainer
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
comment|// Swap updated container with the existing container
name|Container
name|tempContainer
init|=
name|tempRMContainer
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|Resource
name|updatedResource
init|=
name|createUpdatedResource
argument_list|(
name|tempContainer
argument_list|,
name|existingRMContainer
operator|.
name|getContainer
argument_list|()
argument_list|,
name|updateType
argument_list|)
decl_stmt|;
name|Resource
name|resourceToRelease
init|=
name|createResourceToRelease
argument_list|(
name|existingRMContainer
operator|.
name|getContainer
argument_list|()
argument_list|,
name|updateType
argument_list|)
decl_stmt|;
name|Container
name|newContainer
init|=
name|Container
operator|.
name|newInstance
argument_list|(
name|matchedContainerId
argument_list|,
name|existingRMContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|existingRMContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|,
name|updatedResource
argument_list|,
name|existingRMContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|,
literal|null
argument_list|,
name|tempContainer
operator|.
name|getExecutionType
argument_list|()
argument_list|)
decl_stmt|;
name|newContainer
operator|.
name|setAllocationRequestId
argument_list|(
name|existingRMContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|)
expr_stmt|;
name|newContainer
operator|.
name|setVersion
argument_list|(
name|existingRMContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|tempRMContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|setResource
argument_list|(
name|resourceToRelease
argument_list|)
expr_stmt|;
name|tempRMContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|setExecutionType
argument_list|(
name|existingRMContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getExecutionType
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|RMContainerImpl
operator|)
name|existingRMContainer
operator|)
operator|.
name|setContainer
argument_list|(
name|newContainer
argument_list|)
expr_stmt|;
return|return
name|existingRMContainer
return|;
block|}
comment|/**    * Returns the resource that the container will finally be assigned with    * at the end of the update operation.    * @param tempContainer Temporary Container created for the operation.    * @param existingContainer Existing Container.    * @param updateType Update Type.    * @return Final Resource.    */
DECL|method|createUpdatedResource (Container tempContainer, Container existingContainer, ContainerUpdateType updateType)
specifier|private
name|Resource
name|createUpdatedResource
parameter_list|(
name|Container
name|tempContainer
parameter_list|,
name|Container
name|existingContainer
parameter_list|,
name|ContainerUpdateType
name|updateType
parameter_list|)
block|{
if|if
condition|(
name|ContainerUpdateType
operator|.
name|INCREASE_RESOURCE
operator|==
name|updateType
condition|)
block|{
return|return
name|Resources
operator|.
name|add
argument_list|(
name|existingContainer
operator|.
name|getResource
argument_list|()
argument_list|,
name|tempContainer
operator|.
name|getResource
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|ContainerUpdateType
operator|.
name|DECREASE_RESOURCE
operator|==
name|updateType
condition|)
block|{
return|return
name|outstandingDecreases
operator|.
name|get
argument_list|(
name|existingContainer
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|existingContainer
operator|.
name|getResource
argument_list|()
return|;
block|}
block|}
comment|/**    * Returns the resources that need to be released at the end of the update    * operation.    * @param existingContainer Existing Container.    * @param updateType Updated type.    * @return Resources to be released.    */
DECL|method|createResourceToRelease (Container existingContainer, ContainerUpdateType updateType)
specifier|private
name|Resource
name|createResourceToRelease
parameter_list|(
name|Container
name|existingContainer
parameter_list|,
name|ContainerUpdateType
name|updateType
parameter_list|)
block|{
if|if
condition|(
name|ContainerUpdateType
operator|.
name|INCREASE_RESOURCE
operator|==
name|updateType
condition|)
block|{
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|ContainerUpdateType
operator|.
name|DECREASE_RESOURCE
operator|==
name|updateType
condition|)
block|{
return|return
name|Resources
operator|.
name|add
argument_list|(
name|existingContainer
operator|.
name|getResource
argument_list|()
argument_list|,
name|Resources
operator|.
name|negate
argument_list|(
name|outstandingDecreases
operator|.
name|get
argument_list|(
name|existingContainer
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|existingContainer
operator|.
name|getResource
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

