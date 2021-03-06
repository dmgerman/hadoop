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
name|ContainerManagementProtocol
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
name|SerializedException
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
comment|/**  *<p>  * The response sent by the<code>NodeManager</code> to the  *<code>ApplicationMaster</code> when asked to increase container resource.  *</p>  *  * @see ContainerManagementProtocol#increaseContainersResource(IncreaseContainersResourceRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|IncreaseContainersResourceResponse
specifier|public
specifier|abstract
class|class
name|IncreaseContainersResourceResponse
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance ( List<ContainerId> successfullyIncreasedContainers, Map<ContainerId, SerializedException> failedRequests)
specifier|public
specifier|static
name|IncreaseContainersResourceResponse
name|newInstance
parameter_list|(
name|List
argument_list|<
name|ContainerId
argument_list|>
name|successfullyIncreasedContainers
parameter_list|,
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|SerializedException
argument_list|>
name|failedRequests
parameter_list|)
block|{
name|IncreaseContainersResourceResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|IncreaseContainersResourceResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setSuccessfullyIncreasedContainers
argument_list|(
name|successfullyIncreasedContainers
argument_list|)
expr_stmt|;
name|response
operator|.
name|setFailedRequests
argument_list|(
name|failedRequests
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Get the list of containerIds of containers whose resource    * have been successfully increased.    *    * @return the list of containerIds of containers whose resource have    * been successfully increased.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getSuccessfullyIncreasedContainers ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getSuccessfullyIncreasedContainers
parameter_list|()
function_decl|;
comment|/**    * Set the list of containerIds of containers whose resource have    * been successfully increased.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setSuccessfullyIncreasedContainers ( List<ContainerId> succeedIncreasedContainers)
specifier|public
specifier|abstract
name|void
name|setSuccessfullyIncreasedContainers
parameter_list|(
name|List
argument_list|<
name|ContainerId
argument_list|>
name|succeedIncreasedContainers
parameter_list|)
function_decl|;
comment|/**    * Get the containerId-to-exception map in which the exception indicates    * error from each container for failed requests.    * @return map of containerId-to-exception    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getFailedRequests ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|SerializedException
argument_list|>
name|getFailedRequests
parameter_list|()
function_decl|;
comment|/**    * Set the containerId-to-exception map in which the exception indicates    * error from each container for failed requests.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setFailedRequests ( Map<ContainerId, SerializedException> failedRequests)
specifier|public
specifier|abstract
name|void
name|setFailedRequests
parameter_list|(
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|SerializedException
argument_list|>
name|failedRequests
parameter_list|)
function_decl|;
block|}
end_class

end_unit

