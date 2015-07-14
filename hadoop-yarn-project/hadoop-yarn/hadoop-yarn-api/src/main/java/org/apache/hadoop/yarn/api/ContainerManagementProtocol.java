begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
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
name|protocolrecords
operator|.
name|IncreaseContainersResourceRequest
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
name|IncreaseContainersResourceResponse
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
name|GetContainerStatusesRequest
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
name|GetContainerStatusesResponse
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
name|StartContainerRequest
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
name|StartContainersRequest
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
name|StartContainersResponse
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
name|StopContainersRequest
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
name|StopContainersResponse
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
name|ContainerLaunchContext
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
name|exceptions
operator|.
name|NMNotYetReadyException
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

begin_comment
comment|/**  *<p>The protocol between an<code>ApplicationMaster</code> and a   *<code>NodeManager</code> to start/stop and increase resource of containers  * and to get status of running containers.</p>  *  *<p>If security is enabled the<code>NodeManager</code> verifies that the  *<code>ApplicationMaster</code> has truly been allocated the container  * by the<code>ResourceManager</code> and also verifies all interactions such   * as stopping the container or obtaining status information for the container.  *</p>  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|ContainerManagementProtocol
specifier|public
interface|interface
name|ContainerManagementProtocol
block|{
comment|/**    *<p>    * The<code>ApplicationMaster</code> provides a list of    * {@link StartContainerRequest}s to a<code>NodeManager</code> to    *<em>start</em> {@link Container}s allocated to it using this interface.    *</p>    *     *<p>    * The<code>ApplicationMaster</code> has to provide details such as allocated    * resource capability, security tokens (if enabled), command to be executed    * to start the container, environment for the process, necessary    * binaries/jar/shared-objects etc. via the {@link ContainerLaunchContext} in    * the {@link StartContainerRequest}.    *</p>    *     *<p>    * The<code>NodeManager</code> sends a response via    * {@link StartContainersResponse} which includes a list of    * {@link Container}s of successfully launched {@link Container}s, a    * containerId-to-exception map for each failed {@link StartContainerRequest} in    * which the exception indicates errors from per container and a    * allServicesMetaData map between the names of auxiliary services and their    * corresponding meta-data. Note: None-container-specific exceptions will    * still be thrown by the API method itself.    *</p>    *<p>    * The<code>ApplicationMaster</code> can use    * {@link #getContainerStatuses(GetContainerStatusesRequest)} to get updated    * statuses of the to-be-launched or launched containers.    *</p>    *     * @param request    *          request to start a list of containers    * @return response including conatinerIds of all successfully launched    *         containers, a containerId-to-exception map for failed requests and    *         a allServicesMetaData map.    * @throws YarnException    * @throws IOException    * @throws NMNotYetReadyException    *           This exception is thrown when NM starts from scratch but has not    *           yet connected with RM.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|startContainers (StartContainersRequest request)
name|StartContainersResponse
name|startContainers
parameter_list|(
name|StartContainersRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * The<code>ApplicationMaster</code> requests a<code>NodeManager</code> to    *<em>stop</em> a list of {@link Container}s allocated to it using this    * interface.    *</p>    *     *<p>    * The<code>ApplicationMaster</code> sends a {@link StopContainersRequest}    * which includes the {@link ContainerId}s of the containers to be stopped.    *</p>    *     *<p>    * The<code>NodeManager</code> sends a response via    * {@link StopContainersResponse} which includes a list of {@link ContainerId}    * s of successfully stopped containers, a containerId-to-exception map for    * each failed request in which the exception indicates errors from per    * container. Note: None-container-specific exceptions will still be thrown by    * the API method itself.<code>ApplicationMaster</code> can use    * {@link #getContainerStatuses(GetContainerStatusesRequest)} to get updated    * statuses of the containers.    *</p>    *     * @param request    *          request to stop a list of containers    * @return response which includes a list of containerIds of successfully    *         stopped containers, a containerId-to-exception map for failed    *         requests.    * @throws YarnException    * @throws IOException    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|stopContainers (StopContainersRequest request)
name|StopContainersResponse
name|stopContainers
parameter_list|(
name|StopContainersRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * The API used by the<code>ApplicationMaster</code> to request for current    * statuses of<code>Container</code>s from the<code>NodeManager</code>.    *</p>    *     *<p>    * The<code>ApplicationMaster</code> sends a    * {@link GetContainerStatusesRequest} which includes the {@link ContainerId}s    * of all containers whose statuses are needed.    *</p>    *     *<p>    * The<code>NodeManager</code> responds with    * {@link GetContainerStatusesResponse} which includes a list of    * {@link ContainerStatus} of the successfully queried containers and a    * containerId-to-exception map for each failed request in which the exception    * indicates errors from per container. Note: None-container-specific    * exceptions will still be thrown by the API method itself.    *</p>    *     * @param request    *          request to get<code>ContainerStatus</code>es of containers with    *          the specified<code>ContainerId</code>s    * @return response containing the list of<code>ContainerStatus</code> of the    *         successfully queried containers and a containerId-to-exception map    *         for failed requests.    *     * @throws YarnException    * @throws IOException    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getContainerStatuses ( GetContainerStatusesRequest request)
name|GetContainerStatusesResponse
name|getContainerStatuses
parameter_list|(
name|GetContainerStatusesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * The API used by the<code>ApplicationMaster</code> to request for    * resource increase of running containers on the<code>NodeManager</code>.    *</p>    *    * @param request    *         request to increase resource of a list of containers    * @return response which includes a list of containerIds of containers    *         whose resource has been successfully increased and a    *         containerId-to-exception map for failed requests.    *    * @throws YarnException    * @throws IOException    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|increaseContainersResource ( IncreaseContainersResourceRequest request)
name|IncreaseContainersResourceResponse
name|increaseContainersResource
parameter_list|(
name|IncreaseContainersResourceRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

