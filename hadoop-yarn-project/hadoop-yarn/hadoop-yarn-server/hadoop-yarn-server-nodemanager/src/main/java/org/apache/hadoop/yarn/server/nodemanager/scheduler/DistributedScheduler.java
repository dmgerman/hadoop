begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.scheduler
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
name|scheduler
package|;
end_package

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
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|AllocateRequest
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|DistributedSchedulingAllocateRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|DistributedSchedulingAllocateResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterDistributedSchedulingAMResponse
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
name|protocolrecords
operator|.
name|FinishApplicationMasterRequest
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
name|protocolrecords
operator|.
name|FinishApplicationMasterResponse
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterRequest
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterResponse
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
name|NMToken
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
name|exceptions
operator|.
name|YarnException
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
name|api
operator|.
name|protocolrecords
operator|.
name|RemoteNode
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
name|amrmproxy
operator|.
name|AMRMProxyApplicationContext
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
name|amrmproxy
operator|.
name|AbstractRequestInterceptor
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
name|security
operator|.
name|NMTokenSecretManagerInNM
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
name|OpportunisticContainerAllocator
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
name|OpportunisticContainerContext
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
name|ArrayList
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
name|List
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
comment|/**  *<p>The DistributedScheduler runs on the NodeManager and is modeled as an  *<code>AMRMProxy</code> request interceptor. It is responsible for the  * following:</p>  *<ul>  *<li>Intercept<code>ApplicationMasterProtocol</code> calls and unwrap the  *   response objects to extract instructions from the  *<code>ClusterMonitor</code> running on the ResourceManager to aid in making  *   distributed scheduling decisions.</li>  *<li>Call the<code>OpportunisticContainerAllocator</code> to allocate  *   containers for the outstanding OPPORTUNISTIC container requests.</li>  *</ul>  */
end_comment

begin_class
DECL|class|DistributedScheduler
specifier|public
specifier|final
class|class
name|DistributedScheduler
extends|extends
name|AbstractRequestInterceptor
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
name|DistributedScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RECORD_FACTORY
specifier|private
specifier|final
specifier|static
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
DECL|field|oppContainerContext
specifier|private
name|OpportunisticContainerContext
name|oppContainerContext
init|=
operator|new
name|OpportunisticContainerContext
argument_list|()
decl_stmt|;
comment|// Mapping of NodeId to NodeTokens. Populated either from RM response or
comment|// generated locally if required.
DECL|field|nodeTokens
specifier|private
name|Map
argument_list|<
name|NodeId
argument_list|,
name|NMToken
argument_list|>
name|nodeTokens
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|applicationAttemptId
specifier|private
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
DECL|field|containerAllocator
specifier|private
name|OpportunisticContainerAllocator
name|containerAllocator
decl_stmt|;
DECL|field|nmSecretManager
specifier|private
name|NMTokenSecretManagerInNM
name|nmSecretManager
decl_stmt|;
DECL|field|appSubmitter
specifier|private
name|String
name|appSubmitter
decl_stmt|;
DECL|field|rmIdentifier
specifier|private
name|long
name|rmIdentifier
decl_stmt|;
DECL|method|init (AMRMProxyApplicationContext applicationContext)
specifier|public
name|void
name|init
parameter_list|(
name|AMRMProxyApplicationContext
name|applicationContext
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|applicationContext
argument_list|)
expr_stmt|;
name|initLocal
argument_list|(
name|applicationContext
operator|.
name|getNMCotext
argument_list|()
operator|.
name|getNodeStatusUpdater
argument_list|()
operator|.
name|getRMIdentifier
argument_list|()
argument_list|,
name|applicationContext
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|applicationContext
operator|.
name|getNMCotext
argument_list|()
operator|.
name|getContainerAllocator
argument_list|()
argument_list|,
name|applicationContext
operator|.
name|getNMCotext
argument_list|()
operator|.
name|getNMTokenSecretManager
argument_list|()
argument_list|,
name|applicationContext
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|initLocal (long rmId, ApplicationAttemptId appAttemptId, OpportunisticContainerAllocator oppContainerAllocator, NMTokenSecretManagerInNM nmSecretManager, String appSubmitter)
name|void
name|initLocal
parameter_list|(
name|long
name|rmId
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|OpportunisticContainerAllocator
name|oppContainerAllocator
parameter_list|,
name|NMTokenSecretManagerInNM
name|nmSecretManager
parameter_list|,
name|String
name|appSubmitter
parameter_list|)
block|{
name|this
operator|.
name|rmIdentifier
operator|=
name|rmId
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|appAttemptId
expr_stmt|;
name|this
operator|.
name|containerAllocator
operator|=
name|oppContainerAllocator
expr_stmt|;
name|this
operator|.
name|nmSecretManager
operator|=
name|nmSecretManager
expr_stmt|;
name|this
operator|.
name|appSubmitter
operator|=
name|appSubmitter
expr_stmt|;
comment|// Overrides the Generator to decrement container id.
name|this
operator|.
name|oppContainerContext
operator|.
name|setContainerIdGenerator
argument_list|(
operator|new
name|OpportunisticContainerAllocator
operator|.
name|ContainerIdGenerator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|generateContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerIdCounter
operator|.
name|decrementAndGet
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Route register call to the corresponding distributed scheduling method viz.    * registerApplicationMasterForDistributedScheduling, and return response to    * the caller after stripping away Distributed Scheduling information.    *    * @param request    *          registration request    * @return Allocate Response    * @throws YarnException YarnException    * @throws IOException IOException    */
annotation|@
name|Override
DECL|method|registerApplicationMaster (RegisterApplicationMasterRequest request)
specifier|public
name|RegisterApplicationMasterResponse
name|registerApplicationMaster
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|registerApplicationMasterForDistributedScheduling
argument_list|(
name|request
argument_list|)
operator|.
name|getRegisterResponse
argument_list|()
return|;
block|}
comment|/**    * Route allocate call to the allocateForDistributedScheduling method and    * return response to the caller after stripping away Distributed Scheduling    * information.    *    * @param request    *          allocation request    * @return Allocate Response    * @throws YarnException YarnException    * @throws IOException IOException    */
annotation|@
name|Override
DECL|method|allocate (AllocateRequest request)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|DistributedSchedulingAllocateRequest
name|distRequest
init|=
name|RECORD_FACTORY
operator|.
name|newRecordInstance
argument_list|(
name|DistributedSchedulingAllocateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|distRequest
operator|.
name|setAllocateRequest
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|allocateForDistributedScheduling
argument_list|(
name|distRequest
argument_list|)
operator|.
name|getAllocateResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|finishApplicationMaster (FinishApplicationMasterRequest request)
specifier|public
name|FinishApplicationMasterResponse
name|finishApplicationMaster
parameter_list|(
name|FinishApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|getNextInterceptor
argument_list|()
operator|.
name|finishApplicationMaster
argument_list|(
name|request
argument_list|)
return|;
block|}
comment|/**    * Adds all the newly allocated Containers to the allocate Response.    * Additionally, in case the NMToken for one of the nodes does not exist, it    * generates one and adds it to the response.    */
DECL|method|updateAllocateResponse (AllocateResponse response, List<NMToken> nmTokens, List<Container> allocatedContainers)
specifier|private
name|void
name|updateAllocateResponse
parameter_list|(
name|AllocateResponse
name|response
parameter_list|,
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
parameter_list|)
block|{
name|List
argument_list|<
name|NMToken
argument_list|>
name|newTokens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|allocatedContainers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|addAll
argument_list|(
name|allocatedContainers
argument_list|)
expr_stmt|;
for|for
control|(
name|Container
name|alloc
range|:
name|allocatedContainers
control|)
block|{
if|if
condition|(
operator|!
name|nodeTokens
operator|.
name|containsKey
argument_list|(
name|alloc
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
name|newTokens
operator|.
name|add
argument_list|(
name|nmSecretManager
operator|.
name|generateNMToken
argument_list|(
name|appSubmitter
argument_list|,
name|alloc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|NMToken
argument_list|>
name|retTokens
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nmTokens
argument_list|)
decl_stmt|;
name|retTokens
operator|.
name|addAll
argument_list|(
name|newTokens
argument_list|)
expr_stmt|;
name|response
operator|.
name|setNMTokens
argument_list|(
name|retTokens
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|updateParameters ( RegisterDistributedSchedulingAMResponse registerResponse)
specifier|private
name|void
name|updateParameters
parameter_list|(
name|RegisterDistributedSchedulingAMResponse
name|registerResponse
parameter_list|)
block|{
name|Resource
name|incrementResource
init|=
name|registerResponse
operator|.
name|getIncrContainerResource
argument_list|()
decl_stmt|;
if|if
condition|(
name|incrementResource
operator|==
literal|null
condition|)
block|{
name|incrementResource
operator|=
name|registerResponse
operator|.
name|getMinContainerResource
argument_list|()
expr_stmt|;
block|}
name|oppContainerContext
operator|.
name|updateAllocationParams
argument_list|(
name|registerResponse
operator|.
name|getMinContainerResource
argument_list|()
argument_list|,
name|registerResponse
operator|.
name|getMaxContainerResource
argument_list|()
argument_list|,
name|incrementResource
argument_list|,
name|registerResponse
operator|.
name|getContainerTokenExpiryInterval
argument_list|()
argument_list|)
expr_stmt|;
name|oppContainerContext
operator|.
name|getContainerIdGenerator
argument_list|()
operator|.
name|resetContainerIdCounter
argument_list|(
name|registerResponse
operator|.
name|getContainerIdStart
argument_list|()
argument_list|)
expr_stmt|;
name|setNodeList
argument_list|(
name|registerResponse
operator|.
name|getNodesForScheduling
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setNodeList (List<RemoteNode> nodeList)
specifier|private
name|void
name|setNodeList
parameter_list|(
name|List
argument_list|<
name|RemoteNode
argument_list|>
name|nodeList
parameter_list|)
block|{
name|oppContainerContext
operator|.
name|updateNodeList
argument_list|(
name|nodeList
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RegisterDistributedSchedulingAMResponse
DECL|method|registerApplicationMasterForDistributedScheduling ( RegisterApplicationMasterRequest request)
name|registerApplicationMasterForDistributedScheduling
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Forwarding registration request to the"
operator|+
literal|"Distributed Scheduler Service on YARN RM"
argument_list|)
expr_stmt|;
name|RegisterDistributedSchedulingAMResponse
name|dsResp
init|=
name|getNextInterceptor
argument_list|()
operator|.
name|registerApplicationMasterForDistributedScheduling
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|updateParameters
argument_list|(
name|dsResp
argument_list|)
expr_stmt|;
return|return
name|dsResp
return|;
block|}
annotation|@
name|Override
DECL|method|allocateForDistributedScheduling ( DistributedSchedulingAllocateRequest request)
specifier|public
name|DistributedSchedulingAllocateResponse
name|allocateForDistributedScheduling
parameter_list|(
name|DistributedSchedulingAllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// Partition requests to GUARANTEED and OPPORTUNISTIC.
name|OpportunisticContainerAllocator
operator|.
name|PartitionedResourceRequests
name|partitionedAsks
init|=
name|containerAllocator
operator|.
name|partitionAskList
argument_list|(
name|request
operator|.
name|getAllocateRequest
argument_list|()
operator|.
name|getAskList
argument_list|()
argument_list|)
decl_stmt|;
comment|// Allocate OPPORTUNISTIC containers.
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
init|=
name|containerAllocator
operator|.
name|allocateContainers
argument_list|(
name|request
operator|.
name|getAllocateRequest
argument_list|()
operator|.
name|getResourceBlacklistRequest
argument_list|()
argument_list|,
name|partitionedAsks
operator|.
name|getOpportunistic
argument_list|()
argument_list|,
name|applicationAttemptId
argument_list|,
name|oppContainerContext
argument_list|,
name|rmIdentifier
argument_list|,
name|appSubmitter
argument_list|)
decl_stmt|;
comment|// Prepare request for sending to RM for scheduling GUARANTEED containers.
name|request
operator|.
name|setAllocatedContainers
argument_list|(
name|allocatedContainers
argument_list|)
expr_stmt|;
name|request
operator|.
name|getAllocateRequest
argument_list|()
operator|.
name|setAskList
argument_list|(
name|partitionedAsks
operator|.
name|getGuaranteed
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"Forwarding allocate request to the"
operator|+
literal|"Distributed Scheduler Service on YARN RM"
argument_list|)
expr_stmt|;
block|}
name|DistributedSchedulingAllocateResponse
name|dsResp
init|=
name|getNextInterceptor
argument_list|()
operator|.
name|allocateForDistributedScheduling
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// Update host to nodeId mapping
name|setNodeList
argument_list|(
name|dsResp
operator|.
name|getNodesForScheduling
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
init|=
name|dsResp
operator|.
name|getAllocateResponse
argument_list|()
operator|.
name|getNMTokens
argument_list|()
decl_stmt|;
for|for
control|(
name|NMToken
name|nmToken
range|:
name|nmTokens
control|)
block|{
name|nodeTokens
operator|.
name|put
argument_list|(
name|nmToken
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|nmToken
argument_list|)
expr_stmt|;
block|}
comment|// Check if we have NM tokens for all the allocated containers. If not
comment|// generate one and update the response.
name|updateAllocateResponse
argument_list|(
name|dsResp
operator|.
name|getAllocateResponse
argument_list|()
argument_list|,
name|nmTokens
argument_list|,
name|allocatedContainers
argument_list|)
expr_stmt|;
return|return
name|dsResp
return|;
block|}
block|}
end_class

end_unit

