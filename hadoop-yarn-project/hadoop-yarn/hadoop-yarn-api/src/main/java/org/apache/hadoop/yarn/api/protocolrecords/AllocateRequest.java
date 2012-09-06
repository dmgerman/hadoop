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
name|AMRMProtocol
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
name|ResourceRequest
import|;
end_import

begin_comment
comment|/**  *<p>The core request sent by the<code>ApplicationMaster</code> to the   *<code>ResourceManager</code> to obtain resources in the cluster.</p>   *  *<p>The request includes:  *<ul>  *<li>  *         {@link ApplicationAttemptId} being managed by the   *<code>ApplicationMaster</code>  *</li>  *<li>A response id to track duplicate responses.</li>  *<li>Progress information.</li>  *<li>  *       A list of {@link ResourceRequest} to inform the   *<code>ResourceManager</code> about the application's   *       resource requirements.  *</li>  *<li>  *       A list of unused {@link Container} which are being returned.   *</li>  *</ul>  *</p>  *   * @see AMRMProtocol#allocate(AllocateRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|AllocateRequest
specifier|public
interface|interface
name|AllocateRequest
block|{
comment|/**    * Get the<code>ApplicationAttemptId</code> being managed by the     *<code>ApplicationMaster</code>.    * @return<code>ApplicationAttemptId</code> being managed by the     *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationAttemptId ()
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ApplicationAttemptId</code> being managed by the     *<code>ApplicationMaster</code>.    * @param applicationAttemptId<code>ApplicationAttemptId</code> being managed     *                             by the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setApplicationAttemptId (ApplicationAttemptId applicationAttemptId)
name|void
name|setApplicationAttemptId
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
function_decl|;
comment|/**    * Get the<em>response id</em> used to track duplicate responses.    * @return<em>response id</em>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getResponseId ()
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
name|void
name|setProgress
parameter_list|(
name|float
name|progress
parameter_list|)
function_decl|;
comment|/**    * Get the list of<code>ResourceRequest</code> to update the     *<code>ResourceManager</code> about the application's resource requirements.    * @return the list of<code>ResourceRequest</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAskList ()
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|getAskList
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getAsk (int index)
name|ResourceRequest
name|getAsk
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getAskCount ()
name|int
name|getAskCount
parameter_list|()
function_decl|;
comment|/**    * Add list of<code>ResourceRequest</code> to update the     *<code>ResourceManager</code> about the application's resource requirements.    * @param resourceRequest list of<code>ResourceRequest</code> to update the     *<code>ResourceManager</code> about the application's     *                        resource requirements    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|addAllAsks (List<ResourceRequest> resourceRequest)
name|void
name|addAllAsks
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequest
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|addAsk (ResourceRequest request)
name|void
name|addAsk
parameter_list|(
name|ResourceRequest
name|request
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|removeAsk (int index)
name|void
name|removeAsk
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|clearAsks ()
name|void
name|clearAsks
parameter_list|()
function_decl|;
comment|/**    * Get the list of<code>ContainerId</code> of containers being     * released by the<code>ApplicationMaster</code>.    * @return list of<code>ContainerId</code> of containers being     *         released by the<code>ApplicationMaster</code>     */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getReleaseList ()
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getReleaseList
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getRelease (int index)
name|ContainerId
name|getRelease
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getReleaseCount ()
name|int
name|getReleaseCount
parameter_list|()
function_decl|;
comment|/**    * Add the list of<code>ContainerId</code> of containers being     * released by the<code>ApplicationMaster</code>    * @param releaseContainers list of<code>ContainerId</code> of     *                          containers being released by the<    *                          code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|addAllReleases (List<ContainerId> releaseContainers)
name|void
name|addAllReleases
parameter_list|(
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releaseContainers
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|addRelease (ContainerId container)
name|void
name|addRelease
parameter_list|(
name|ContainerId
name|container
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|removeRelease (int index)
name|void
name|removeRelease
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|clearReleases ()
name|void
name|clearReleases
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

