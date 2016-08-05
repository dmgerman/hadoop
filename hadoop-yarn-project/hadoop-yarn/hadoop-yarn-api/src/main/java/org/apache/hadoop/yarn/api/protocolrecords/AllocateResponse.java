begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
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
name|protocolrecords
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
name|Evolving
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
name|api
operator|.
name|ApplicationMasterProtocol
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
name|AMCommand
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
name|NodeReport
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
name|PreemptionMessage
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
name|Priority
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
name|Token
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

begin_comment
comment|/**  * The response sent by the<code>ResourceManager</code> the  *<code>ApplicationMaster</code> during resource negotiation.  *<p>  * The response, includes:  *<ul>  *<li>Response ID to track duplicate responses.</li>  *<li>  *     An AMCommand sent by ResourceManager to let the  *     {@code ApplicationMaster} take some actions (resync, shutdown etc.).  *</li>  *<li>A list of newly allocated {@link Container}.</li>  *<li>A list of completed {@link Container}s' statuses.</li>  *<li>  *     The available headroom for resources in the cluster for the  *     application.  *</li>  *<li>A list of nodes whose status has been updated.</li>  *<li>The number of available nodes in a cluster.</li>  *<li>A description of resources requested back by the cluster</li>  *<li>AMRMToken, if AMRMToken has been rolled over</li>  *<li>  *     A list of {@link Container} representing the containers  *     whose resource has been increased.  *</li>  *<li>  *     A list of {@link Container} representing the containers  *     whose resource has been decreased.  *</li>  *</ul>  *   * @see ApplicationMasterProtocol#allocate(AllocateRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|AllocateResponse
specifier|public
specifier|abstract
class|class
name|AllocateResponse
block|{
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance (int responseId, List<ContainerStatus> completedContainers, List<Container> allocatedContainers, List<NodeReport> updatedNodes, Resource availResources, AMCommand command, int numClusterNodes, PreemptionMessage preempt, List<NMToken> nmTokens)
specifier|public
specifier|static
name|AllocateResponse
name|newInstance
parameter_list|(
name|int
name|responseId
parameter_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completedContainers
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
parameter_list|,
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|,
name|Resource
name|availResources
parameter_list|,
name|AMCommand
name|command
parameter_list|,
name|int
name|numClusterNodes
parameter_list|,
name|PreemptionMessage
name|preempt
parameter_list|,
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
parameter_list|)
block|{
name|AllocateResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setNumClusterNodes
argument_list|(
name|numClusterNodes
argument_list|)
expr_stmt|;
name|response
operator|.
name|setResponseId
argument_list|(
name|responseId
argument_list|)
expr_stmt|;
name|response
operator|.
name|setCompletedContainersStatuses
argument_list|(
name|completedContainers
argument_list|)
expr_stmt|;
name|response
operator|.
name|setAllocatedContainers
argument_list|(
name|allocatedContainers
argument_list|)
expr_stmt|;
name|response
operator|.
name|setUpdatedNodes
argument_list|(
name|updatedNodes
argument_list|)
expr_stmt|;
name|response
operator|.
name|setAvailableResources
argument_list|(
name|availResources
argument_list|)
expr_stmt|;
name|response
operator|.
name|setAMCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|response
operator|.
name|setPreemptionMessage
argument_list|(
name|preempt
argument_list|)
expr_stmt|;
name|response
operator|.
name|setNMTokens
argument_list|(
name|nmTokens
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance (int responseId, List<ContainerStatus> completedContainers, List<Container> allocatedContainers, List<NodeReport> updatedNodes, Resource availResources, AMCommand command, int numClusterNodes, PreemptionMessage preempt, List<NMToken> nmTokens, List<Container> increasedContainers, List<Container> decreasedContainers)
specifier|public
specifier|static
name|AllocateResponse
name|newInstance
parameter_list|(
name|int
name|responseId
parameter_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completedContainers
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
parameter_list|,
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|,
name|Resource
name|availResources
parameter_list|,
name|AMCommand
name|command
parameter_list|,
name|int
name|numClusterNodes
parameter_list|,
name|PreemptionMessage
name|preempt
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
name|increasedContainers
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|decreasedContainers
parameter_list|)
block|{
name|AllocateResponse
name|response
init|=
name|newInstance
argument_list|(
name|responseId
argument_list|,
name|completedContainers
argument_list|,
name|allocatedContainers
argument_list|,
name|updatedNodes
argument_list|,
name|availResources
argument_list|,
name|command
argument_list|,
name|numClusterNodes
argument_list|,
name|preempt
argument_list|,
name|nmTokens
argument_list|)
decl_stmt|;
name|response
operator|.
name|setIncreasedContainers
argument_list|(
name|increasedContainers
argument_list|)
expr_stmt|;
name|response
operator|.
name|setDecreasedContainers
argument_list|(
name|decreasedContainers
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (int responseId, List<ContainerStatus> completedContainers, List<Container> allocatedContainers, List<NodeReport> updatedNodes, Resource availResources, AMCommand command, int numClusterNodes, PreemptionMessage preempt, List<NMToken> nmTokens, Token amRMToken, List<Container> increasedContainers, List<Container> decreasedContainers)
specifier|public
specifier|static
name|AllocateResponse
name|newInstance
parameter_list|(
name|int
name|responseId
parameter_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completedContainers
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
parameter_list|,
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|,
name|Resource
name|availResources
parameter_list|,
name|AMCommand
name|command
parameter_list|,
name|int
name|numClusterNodes
parameter_list|,
name|PreemptionMessage
name|preempt
parameter_list|,
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
parameter_list|,
name|Token
name|amRMToken
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|increasedContainers
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|decreasedContainers
parameter_list|)
block|{
name|AllocateResponse
name|response
init|=
name|newInstance
argument_list|(
name|responseId
argument_list|,
name|completedContainers
argument_list|,
name|allocatedContainers
argument_list|,
name|updatedNodes
argument_list|,
name|availResources
argument_list|,
name|command
argument_list|,
name|numClusterNodes
argument_list|,
name|preempt
argument_list|,
name|nmTokens
argument_list|,
name|increasedContainers
argument_list|,
name|decreasedContainers
argument_list|)
decl_stmt|;
name|response
operator|.
name|setAMRMToken
argument_list|(
name|amRMToken
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance (int responseId, List<ContainerStatus> completedContainers, List<Container> allocatedContainers, List<NodeReport> updatedNodes, Resource availResources, AMCommand command, int numClusterNodes, PreemptionMessage preempt, List<NMToken> nmTokens, Token amRMToken, List<Container> increasedContainers, List<Container> decreasedContainers, String collectorAddr)
specifier|public
specifier|static
name|AllocateResponse
name|newInstance
parameter_list|(
name|int
name|responseId
parameter_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completedContainers
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
parameter_list|,
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|,
name|Resource
name|availResources
parameter_list|,
name|AMCommand
name|command
parameter_list|,
name|int
name|numClusterNodes
parameter_list|,
name|PreemptionMessage
name|preempt
parameter_list|,
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
parameter_list|,
name|Token
name|amRMToken
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|increasedContainers
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|decreasedContainers
parameter_list|,
name|String
name|collectorAddr
parameter_list|)
block|{
name|AllocateResponse
name|response
init|=
name|newInstance
argument_list|(
name|responseId
argument_list|,
name|completedContainers
argument_list|,
name|allocatedContainers
argument_list|,
name|updatedNodes
argument_list|,
name|availResources
argument_list|,
name|command
argument_list|,
name|numClusterNodes
argument_list|,
name|preempt
argument_list|,
name|nmTokens
argument_list|,
name|increasedContainers
argument_list|,
name|decreasedContainers
argument_list|)
decl_stmt|;
name|response
operator|.
name|setAMRMToken
argument_list|(
name|amRMToken
argument_list|)
expr_stmt|;
name|response
operator|.
name|setCollectorAddr
argument_list|(
name|collectorAddr
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * If the<code>ResourceManager</code> needs the    *<code>ApplicationMaster</code> to take some action then it will send an    * AMCommand to the<code>ApplicationMaster</code>. See<code>AMCommand</code>     * for details on commands and actions for them.    * @return<code>AMCommand</code> if the<code>ApplicationMaster</code> should    *         take action,<code>null</code> otherwise    * @see AMCommand    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAMCommand ()
specifier|public
specifier|abstract
name|AMCommand
name|getAMCommand
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAMCommand (AMCommand command)
specifier|public
specifier|abstract
name|void
name|setAMCommand
parameter_list|(
name|AMCommand
name|command
parameter_list|)
function_decl|;
comment|/**    * Get the<em>last response id</em>.    * @return<em>last response id</em>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getResponseId ()
specifier|public
specifier|abstract
name|int
name|getResponseId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setResponseId (int responseId)
specifier|public
specifier|abstract
name|void
name|setResponseId
parameter_list|(
name|int
name|responseId
parameter_list|)
function_decl|;
comment|/**    * Get the list of<em>newly allocated</em><code>Container</code> by the    *<code>ResourceManager</code>.    * @return list of<em>newly allocated</em><code>Container</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAllocatedContainers ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|Container
argument_list|>
name|getAllocatedContainers
parameter_list|()
function_decl|;
comment|/**    * Set the list of<em>newly allocated</em><code>Container</code> by the    *<code>ResourceManager</code>.    * @param containers list of<em>newly allocated</em><code>Container</code>    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAllocatedContainers (List<Container> containers)
specifier|public
specifier|abstract
name|void
name|setAllocatedContainers
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
function_decl|;
comment|/**    * Get the<em>available headroom</em> for resources in the cluster for the    * application.    * @return limit of available headroom for resources in the cluster for the    * application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAvailableResources ()
specifier|public
specifier|abstract
name|Resource
name|getAvailableResources
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAvailableResources (Resource limit)
specifier|public
specifier|abstract
name|void
name|setAvailableResources
parameter_list|(
name|Resource
name|limit
parameter_list|)
function_decl|;
comment|/**    * Get the list of<em>completed containers' statuses</em>.    * @return the list of<em>completed containers' statuses</em>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCompletedContainersStatuses ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getCompletedContainersStatuses
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCompletedContainersStatuses (List<ContainerStatus> containers)
specifier|public
specifier|abstract
name|void
name|setCompletedContainersStatuses
parameter_list|(
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|containers
parameter_list|)
function_decl|;
comment|/**    * Get the list of<em>updated<code>NodeReport</code>s</em>. Updates could    * be changes in health, availability etc of the nodes.    * @return The delta of updated nodes since the last response    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getUpdatedNodes ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getUpdatedNodes
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setUpdatedNodes (final List<NodeReport> updatedNodes)
specifier|public
specifier|abstract
name|void
name|setUpdatedNodes
parameter_list|(
specifier|final
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|)
function_decl|;
comment|/**    * Get the number of hosts available on the cluster.    * @return the available host count.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNumClusterNodes ()
specifier|public
specifier|abstract
name|int
name|getNumClusterNodes
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumClusterNodes (int numNodes)
specifier|public
specifier|abstract
name|void
name|setNumClusterNodes
parameter_list|(
name|int
name|numNodes
parameter_list|)
function_decl|;
comment|/**    * Get the description of containers owned by the AM, but requested back by    * the cluster. Note that the RM may have an inconsistent view of the    * resources owned by the AM. These messages are advisory, and the AM may    * elect to ignore them.    *<p>    * The message is a snapshot of the resources the RM wants back from the AM.    * While demand persists, the RM will repeat its request; applications should    * not interpret each message as a request for<em>additional</em>    * resources on top of previous messages. Resources requested consistently    * over some duration may be forcibly killed by the RM.    *    * @return A specification of the resources to reclaim from this AM.    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getPreemptionMessage ()
specifier|public
specifier|abstract
name|PreemptionMessage
name|getPreemptionMessage
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setPreemptionMessage (PreemptionMessage request)
specifier|public
specifier|abstract
name|void
name|setPreemptionMessage
parameter_list|(
name|PreemptionMessage
name|request
parameter_list|)
function_decl|;
comment|/**    * Get the list of NMTokens required for communicating with NM. New NMTokens    * issued only if    *<p>    * 1) AM is receiving first container on underlying NodeManager.<br>    * OR<br>    * 2) NMToken master key rolled over in ResourceManager and AM is getting new    * container on the same underlying NodeManager.    *<p>    * AM will receive one NMToken per NM irrespective of the number of containers    * issued on same NM. AM is expected to store these tokens until issued a    * new token for the same NM.    * @return list of NMTokens required for communicating with NM    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNMTokens ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|NMToken
argument_list|>
name|getNMTokens
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNMTokens (List<NMToken> nmTokens)
specifier|public
specifier|abstract
name|void
name|setNMTokens
parameter_list|(
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
parameter_list|)
function_decl|;
comment|/**    * Get the list of newly increased containers by    *<code>ResourceManager</code>.    * @return list of newly increased containers    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getIncreasedContainers ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|Container
argument_list|>
name|getIncreasedContainers
parameter_list|()
function_decl|;
comment|/**    * Set the list of newly increased containers by    *<code>ResourceManager</code>.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setIncreasedContainers ( List<Container> increasedContainers)
specifier|public
specifier|abstract
name|void
name|setIncreasedContainers
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|increasedContainers
parameter_list|)
function_decl|;
comment|/**    * Get the list of newly decreased containers by    *<code>ResourceManager</code>.    * @return the list of newly decreased containers    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getDecreasedContainers ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|Container
argument_list|>
name|getDecreasedContainers
parameter_list|()
function_decl|;
comment|/**    * Set the list of newly decreased containers by    *<code>ResourceManager</code>.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setDecreasedContainers ( List<Container> decreasedContainers)
specifier|public
specifier|abstract
name|void
name|setDecreasedContainers
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|decreasedContainers
parameter_list|)
function_decl|;
comment|/**    * The AMRMToken that belong to this attempt    *    * @return The AMRMToken that belong to this attempt    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAMRMToken ()
specifier|public
specifier|abstract
name|Token
name|getAMRMToken
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAMRMToken (Token amRMToken)
specifier|public
specifier|abstract
name|void
name|setAMRMToken
parameter_list|(
name|Token
name|amRMToken
parameter_list|)
function_decl|;
comment|/**    * Priority of the application    *    * @return get application priority    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getApplicationPriority ()
specifier|public
specifier|abstract
name|Priority
name|getApplicationPriority
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationPriority (Priority priority)
specifier|public
specifier|abstract
name|void
name|setApplicationPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
function_decl|;
comment|/**    * The address of collector that belong to this app    *    * @return The address of collector that belong to this attempt    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getCollectorAddr ()
specifier|public
specifier|abstract
name|String
name|getCollectorAddr
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCollectorAddr (String collectorAddr)
specifier|public
specifier|abstract
name|void
name|setCollectorAddr
parameter_list|(
name|String
name|collectorAddr
parameter_list|)
function_decl|;
block|}
end_class

end_unit

