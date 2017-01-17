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
name|ResourceBlacklistRequest
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>The core request sent by the<code>ApplicationMaster</code> to the   *<code>ResourceManager</code> to obtain resources in the cluster.</p>   *  *<p>The request includes:  *<ul>  *<li>A response id to track duplicate responses.</li>  *<li>Progress information.</li>  *<li>  *     A list of {@link ResourceRequest} to inform the  *<code>ResourceManager</code> about the application's  *     resource requirements.  *</li>  *<li>  *     A list of unused {@link Container} which are being returned.  *</li>  *<li>  *     A list of {@link UpdateContainerRequest} to inform  *     the<code>ResourceManager</code> about the change in  *     requirements of running containers.  *</li>  *</ul>  *   * @see ApplicationMasterProtocol#allocate(AllocateRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|AllocateRequest
specifier|public
specifier|abstract
class|class
name|AllocateRequest
block|{
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance (int responseID, float appProgress, List<ResourceRequest> resourceAsk, List<ContainerId> containersToBeReleased, ResourceBlacklistRequest resourceBlacklistRequest)
specifier|public
specifier|static
name|AllocateRequest
name|newInstance
parameter_list|(
name|int
name|responseID
parameter_list|,
name|float
name|appProgress
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceAsk
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containersToBeReleased
parameter_list|,
name|ResourceBlacklistRequest
name|resourceBlacklistRequest
parameter_list|)
block|{
return|return
name|AllocateRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|responseId
argument_list|(
name|responseID
argument_list|)
operator|.
name|progress
argument_list|(
name|appProgress
argument_list|)
operator|.
name|askList
argument_list|(
name|resourceAsk
argument_list|)
operator|.
name|releaseList
argument_list|(
name|containersToBeReleased
argument_list|)
operator|.
name|resourceBlacklistRequest
argument_list|(
name|resourceBlacklistRequest
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance (int responseID, float appProgress, List<ResourceRequest> resourceAsk, List<ContainerId> containersToBeReleased, List<UpdateContainerRequest> updateRequests, ResourceBlacklistRequest resourceBlacklistRequest)
specifier|public
specifier|static
name|AllocateRequest
name|newInstance
parameter_list|(
name|int
name|responseID
parameter_list|,
name|float
name|appProgress
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceAsk
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containersToBeReleased
parameter_list|,
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|updateRequests
parameter_list|,
name|ResourceBlacklistRequest
name|resourceBlacklistRequest
parameter_list|)
block|{
return|return
name|AllocateRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|responseId
argument_list|(
name|responseID
argument_list|)
operator|.
name|progress
argument_list|(
name|appProgress
argument_list|)
operator|.
name|askList
argument_list|(
name|resourceAsk
argument_list|)
operator|.
name|releaseList
argument_list|(
name|containersToBeReleased
argument_list|)
operator|.
name|resourceBlacklistRequest
argument_list|(
name|resourceBlacklistRequest
argument_list|)
operator|.
name|updateRequests
argument_list|(
name|updateRequests
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Get the<em>response id</em> used to track duplicate responses.    * @return<em>response id</em>    */
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
comment|/**    * Set the<em>response id</em> used to track duplicate responses.    * @param id<em>response id</em>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setResponseId (int id)
specifier|public
specifier|abstract
name|void
name|setResponseId
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
comment|/**    * Get the<em>current progress</em> of application.     * @return<em>current progress</em> of application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getProgress ()
specifier|public
specifier|abstract
name|float
name|getProgress
parameter_list|()
function_decl|;
comment|/**    * Set the<em>current progress</em> of application    * @param progress<em>current progress</em> of application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setProgress (float progress)
specifier|public
specifier|abstract
name|void
name|setProgress
parameter_list|(
name|float
name|progress
parameter_list|)
function_decl|;
comment|/**    * Get the list of<code>ResourceRequest</code> to update the     *<code>ResourceManager</code> about the application's resource requirements.    * @return the list of<code>ResourceRequest</code>    * @see ResourceRequest    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAskList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|getAskList
parameter_list|()
function_decl|;
comment|/**    * Set list of<code>ResourceRequest</code> to update the    *<code>ResourceManager</code> about the application's resource requirements.    * @param resourceRequests list of<code>ResourceRequest</code> to update the     *<code>ResourceManager</code> about the application's     *                        resource requirements    * @see ResourceRequest    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setAskList (List<ResourceRequest> resourceRequests)
specifier|public
specifier|abstract
name|void
name|setAskList
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequests
parameter_list|)
function_decl|;
comment|/**    * Get the list of<code>ContainerId</code> of containers being     * released by the<code>ApplicationMaster</code>.    * @return list of<code>ContainerId</code> of containers being     *         released by the<code>ApplicationMaster</code>     */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getReleaseList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getReleaseList
parameter_list|()
function_decl|;
comment|/**    * Set the list of<code>ContainerId</code> of containers being    * released by the<code>ApplicationMaster</code>    * @param releaseContainers list of<code>ContainerId</code> of     *                          containers being released by the     *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setReleaseList (List<ContainerId> releaseContainers)
specifier|public
specifier|abstract
name|void
name|setReleaseList
parameter_list|(
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releaseContainers
parameter_list|)
function_decl|;
comment|/**    * Get the<code>ResourceBlacklistRequest</code> being sent by the     *<code>ApplicationMaster</code>.    * @return the<code>ResourceBlacklistRequest</code> being sent by the     *<code>ApplicationMaster</code>    * @see ResourceBlacklistRequest    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getResourceBlacklistRequest ()
specifier|public
specifier|abstract
name|ResourceBlacklistRequest
name|getResourceBlacklistRequest
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ResourceBlacklistRequest</code> to inform the     *<code>ResourceManager</code> about the blacklist additions and removals    * per the<code>ApplicationMaster</code>.    *     * @param resourceBlacklistRequest the<code>ResourceBlacklistRequest</code>      *                         to inform the<code>ResourceManager</code> about      *                         the blacklist additions and removals    *                         per the<code>ApplicationMaster</code>    * @see ResourceBlacklistRequest    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setResourceBlacklistRequest ( ResourceBlacklistRequest resourceBlacklistRequest)
specifier|public
specifier|abstract
name|void
name|setResourceBlacklistRequest
parameter_list|(
name|ResourceBlacklistRequest
name|resourceBlacklistRequest
parameter_list|)
function_decl|;
comment|/**    * Get the list of container update requests being sent by the    *<code>ApplicationMaster</code>.    * @return list of {@link UpdateContainerRequest}    *         being sent by the    *<code>ApplicationMaster</code>.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getUpdateRequests ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|getUpdateRequests
parameter_list|()
function_decl|;
comment|/**    * Set the list of container update requests to inform the    *<code>ResourceManager</code> about the containers that need to be    * updated.    * @param updateRequests list of<code>UpdateContainerRequest</code> for    *                       containers to be updated    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setUpdateRequests ( List<UpdateContainerRequest> updateRequests)
specifier|public
specifier|abstract
name|void
name|setUpdateRequests
parameter_list|(
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|updateRequests
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newBuilder ()
specifier|public
specifier|static
name|AllocateRequestBuilder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|AllocateRequestBuilder
argument_list|()
return|;
block|}
comment|/**    * Class to construct instances of {@link AllocateRequest} with specific    * options.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|AllocateRequestBuilder
specifier|public
specifier|static
specifier|final
class|class
name|AllocateRequestBuilder
block|{
DECL|field|allocateRequest
specifier|private
name|AllocateRequest
name|allocateRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|AllocateRequestBuilder ()
specifier|private
name|AllocateRequestBuilder
parameter_list|()
block|{     }
comment|/**      * Set the<code>responseId</code> of the request.      * @see AllocateRequest#setResponseId(int)      * @param responseId<code>responseId</code> of the request      * @return {@link AllocateRequestBuilder}      */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|responseId (int responseId)
specifier|public
name|AllocateRequestBuilder
name|responseId
parameter_list|(
name|int
name|responseId
parameter_list|)
block|{
name|allocateRequest
operator|.
name|setResponseId
argument_list|(
name|responseId
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the<code>progress</code> of the request.      * @see AllocateRequest#setProgress(float)      * @param progress<code>progress</code> of the request      * @return {@link AllocateRequestBuilder}      */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|progress (float progress)
specifier|public
name|AllocateRequestBuilder
name|progress
parameter_list|(
name|float
name|progress
parameter_list|)
block|{
name|allocateRequest
operator|.
name|setProgress
argument_list|(
name|progress
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the<code>askList</code> of the request.      * @see AllocateRequest#setAskList(List)      * @param askList<code>askList</code> of the request      * @return {@link AllocateRequestBuilder}      */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|askList (List<ResourceRequest> askList)
specifier|public
name|AllocateRequestBuilder
name|askList
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|askList
parameter_list|)
block|{
name|allocateRequest
operator|.
name|setAskList
argument_list|(
name|askList
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the<code>releaseList</code> of the request.      * @see AllocateRequest#setReleaseList(List)      * @param releaseList<code>releaseList</code> of the request      * @return {@link AllocateRequestBuilder}      */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|releaseList (List<ContainerId> releaseList)
specifier|public
name|AllocateRequestBuilder
name|releaseList
parameter_list|(
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releaseList
parameter_list|)
block|{
name|allocateRequest
operator|.
name|setReleaseList
argument_list|(
name|releaseList
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the<code>resourceBlacklistRequest</code> of the request.      * @see AllocateRequest#setResourceBlacklistRequest(      * ResourceBlacklistRequest)      * @param resourceBlacklistRequest      *<code>resourceBlacklistRequest</code> of the request      * @return {@link AllocateRequestBuilder}      */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|resourceBlacklistRequest ( ResourceBlacklistRequest resourceBlacklistRequest)
specifier|public
name|AllocateRequestBuilder
name|resourceBlacklistRequest
parameter_list|(
name|ResourceBlacklistRequest
name|resourceBlacklistRequest
parameter_list|)
block|{
name|allocateRequest
operator|.
name|setResourceBlacklistRequest
argument_list|(
name|resourceBlacklistRequest
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the<code>updateRequests</code> of the request.      * @see AllocateRequest#setUpdateRequests(List)      * @param updateRequests<code>updateRequests</code> of the request      * @return {@link AllocateRequestBuilder}      */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|updateRequests ( List<UpdateContainerRequest> updateRequests)
specifier|public
name|AllocateRequestBuilder
name|updateRequests
parameter_list|(
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|updateRequests
parameter_list|)
block|{
name|allocateRequest
operator|.
name|setUpdateRequests
argument_list|(
name|updateRequests
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Return generated {@link AllocateRequest} object.      * @return {@link AllocateRequest}      */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|build ()
specifier|public
name|AllocateRequest
name|build
parameter_list|()
block|{
return|return
name|allocateRequest
return|;
block|}
block|}
block|}
end_class

end_unit

