begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|concurrent
operator|.
name|ConcurrentMap
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
name|service
operator|.
name|AbstractService
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
name|FinalApplicationStatus
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
name|client
operator|.
name|api
operator|.
name|impl
operator|.
name|AMRMClientImpl
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|AMRMClient
specifier|public
specifier|abstract
class|class
name|AMRMClient
parameter_list|<
name|T
extends|extends
name|AMRMClient
operator|.
name|ContainerRequest
parameter_list|>
extends|extends
name|AbstractService
block|{
comment|/**    * Create a new instance of AMRMClient.    * For usage:    *<pre>    * {@code    * AMRMClient.<T>createAMRMClientContainerRequest(appAttemptId)    * }</pre>    * @param appAttemptId the appAttemptId associated with the AMRMClient    * @return the newly create AMRMClient instance.    */
annotation|@
name|Public
DECL|method|createAMRMClient ( ApplicationAttemptId appAttemptId)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|ContainerRequest
parameter_list|>
name|AMRMClient
argument_list|<
name|T
argument_list|>
name|createAMRMClient
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
block|{
name|AMRMClient
argument_list|<
name|T
argument_list|>
name|client
init|=
operator|new
name|AMRMClientImpl
argument_list|<
name|T
argument_list|>
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
return|return
name|client
return|;
block|}
annotation|@
name|Private
DECL|method|AMRMClient (String name)
specifier|protected
name|AMRMClient
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Object to represent container request for resources. Scheduler    * documentation should be consulted for the specifics of how the parameters    * are honored.    * All getters return immutable values.    *     * @param capability    *    The {@link Resource} to be requested for each container.    * @param nodes    *    Any hosts to request that the containers are placed on.    * @param racks    *    Any racks to request that the containers are placed on. The racks    *    corresponding to any hosts requested will be automatically added to    *    this list.    * @param priority    *    The priority at which to request the containers. Higher priorities have    *    lower numerical values.    * @param containerCount    *    The number of containers to request.    */
DECL|class|ContainerRequest
specifier|public
specifier|static
class|class
name|ContainerRequest
block|{
DECL|field|capability
specifier|final
name|Resource
name|capability
decl_stmt|;
DECL|field|nodes
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|nodes
decl_stmt|;
DECL|field|racks
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|racks
decl_stmt|;
DECL|field|priority
specifier|final
name|Priority
name|priority
decl_stmt|;
DECL|field|containerCount
specifier|final
name|int
name|containerCount
decl_stmt|;
DECL|method|ContainerRequest (Resource capability, String[] nodes, String[] racks, Priority priority, int containerCount)
specifier|public
name|ContainerRequest
parameter_list|(
name|Resource
name|capability
parameter_list|,
name|String
index|[]
name|nodes
parameter_list|,
name|String
index|[]
name|racks
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|int
name|containerCount
parameter_list|)
block|{
name|this
operator|.
name|capability
operator|=
name|capability
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
operator|(
name|nodes
operator|!=
literal|null
condition|?
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|nodes
argument_list|)
else|:
literal|null
operator|)
expr_stmt|;
name|this
operator|.
name|racks
operator|=
operator|(
name|racks
operator|!=
literal|null
condition|?
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|racks
argument_list|)
else|:
literal|null
operator|)
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|this
operator|.
name|containerCount
operator|=
name|containerCount
expr_stmt|;
block|}
DECL|method|getCapability ()
specifier|public
name|Resource
name|getCapability
parameter_list|()
block|{
return|return
name|capability
return|;
block|}
DECL|method|getNodes ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getNodes
parameter_list|()
block|{
return|return
name|nodes
return|;
block|}
DECL|method|getRacks ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getRacks
parameter_list|()
block|{
return|return
name|racks
return|;
block|}
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
DECL|method|getContainerCount ()
specifier|public
name|int
name|getContainerCount
parameter_list|()
block|{
return|return
name|containerCount
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Capability["
argument_list|)
operator|.
name|append
argument_list|(
name|capability
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Priority["
argument_list|)
operator|.
name|append
argument_list|(
name|priority
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ContainerCount["
argument_list|)
operator|.
name|append
argument_list|(
name|containerCount
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * This creates a<code>ContainerRequest</code> for 1 container and the    * AMRMClient stores this request internally.<code>getMatchingRequests</code>    * can be used to retrieve these requests from AMRMClient. These requests may     * be matched with an allocated container to determine which request to assign    * the container to.<code>removeContainerRequest</code> must be called using     * the same assigned<code>StoredContainerRequest</code> object so that     * AMRMClient can remove it from its internal store.    */
DECL|class|StoredContainerRequest
specifier|public
specifier|static
class|class
name|StoredContainerRequest
extends|extends
name|ContainerRequest
block|{
DECL|method|StoredContainerRequest (Resource capability, String[] nodes, String[] racks, Priority priority)
specifier|public
name|StoredContainerRequest
parameter_list|(
name|Resource
name|capability
parameter_list|,
name|String
index|[]
name|nodes
parameter_list|,
name|String
index|[]
name|racks
parameter_list|,
name|Priority
name|priority
parameter_list|)
block|{
name|super
argument_list|(
name|capability
argument_list|,
name|nodes
argument_list|,
name|racks
argument_list|,
name|priority
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Register the application master. This must be called before any     * other interaction    * @param appHostName Name of the host on which master is running    * @param appHostPort Port master is listening on    * @param appTrackingUrl URL at which the master info can be seen    * @return<code>RegisterApplicationMasterResponse</code>    * @throws YarnException    * @throws IOException    */
specifier|public
specifier|abstract
name|RegisterApplicationMasterResponse
DECL|method|registerApplicationMaster (String appHostName, int appHostPort, String appTrackingUrl)
name|registerApplicationMaster
parameter_list|(
name|String
name|appHostName
parameter_list|,
name|int
name|appHostPort
parameter_list|,
name|String
name|appTrackingUrl
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Request additional containers and receive new container allocations.    * Requests made via<code>addContainerRequest</code> are sent to the     *<code>ResourceManager</code>. New containers assigned to the master are     * retrieved. Status of completed containers and node health updates are     * also retrieved.    * This also doubles up as a heartbeat to the ResourceManager and must be     * made periodically.    * The call may not always return any new allocations of containers.    * App should not make concurrent allocate requests. May cause request loss.    * @param progressIndicator Indicates progress made by the master    * @return the response of the allocate request    * @throws YarnException    * @throws IOException    */
DECL|method|allocate (float progressIndicator)
specifier|public
specifier|abstract
name|AllocateResponse
name|allocate
parameter_list|(
name|float
name|progressIndicator
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Unregister the application master. This must be called in the end.    * @param appStatus Success/Failure status of the master    * @param appMessage Diagnostics message on failure    * @param appTrackingUrl New URL to get master info    * @throws YarnException    * @throws IOException    */
DECL|method|unregisterApplicationMaster (FinalApplicationStatus appStatus, String appMessage, String appTrackingUrl)
specifier|public
specifier|abstract
name|void
name|unregisterApplicationMaster
parameter_list|(
name|FinalApplicationStatus
name|appStatus
parameter_list|,
name|String
name|appMessage
parameter_list|,
name|String
name|appTrackingUrl
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Request containers for resources before calling<code>allocate</code>    * @param req Resource request    */
DECL|method|addContainerRequest (T req)
specifier|public
specifier|abstract
name|void
name|addContainerRequest
parameter_list|(
name|T
name|req
parameter_list|)
function_decl|;
comment|/**    * Remove previous container request. The previous container request may have     * already been sent to the ResourceManager. So even after the remove request     * the app must be prepared to receive an allocation for the previous request     * even after the remove request    * @param req Resource request    */
DECL|method|removeContainerRequest (T req)
specifier|public
specifier|abstract
name|void
name|removeContainerRequest
parameter_list|(
name|T
name|req
parameter_list|)
function_decl|;
comment|/**    * Release containers assigned by the Resource Manager. If the app cannot use    * the container or wants to give up the container then it can release them.    * The app needs to make new requests for the released resource capability if    * it still needs it. eg. it released non-local resources    * @param containerId    */
DECL|method|releaseAssignedContainer (ContainerId containerId)
specifier|public
specifier|abstract
name|void
name|releaseAssignedContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Get the currently available resources in the cluster.    * A valid value is available after a call to allocate has been made    * @return Currently available resources    */
DECL|method|getAvailableResources ()
specifier|public
specifier|abstract
name|Resource
name|getAvailableResources
parameter_list|()
function_decl|;
comment|/**    * Get the current number of nodes in the cluster.    * A valid values is available after a call to allocate has been made    * @return Current number of nodes in the cluster    */
DECL|method|getClusterNodeCount ()
specifier|public
specifier|abstract
name|int
name|getClusterNodeCount
parameter_list|()
function_decl|;
comment|/**    * Get outstanding<code>StoredContainerRequest</code>s matching the given     * parameters. These StoredContainerRequests should have been added via    *<code>addContainerRequest</code> earlier in the lifecycle. For performance,    * the AMRMClient may return its internal collection directly without creating     * a copy. Users should not perform mutable operations on the return value.    * Each collection in the list contains requests with identical     *<code>Resource</code> size that fit in the given capability. In a     * collection, requests will be returned in the same order as they were added.    * @return Collection of request matching the parameters    */
DECL|method|getMatchingRequests ( Priority priority, String resourceName, Resource capability)
specifier|public
specifier|abstract
name|List
argument_list|<
name|?
extends|extends
name|Collection
argument_list|<
name|T
argument_list|>
argument_list|>
name|getMatchingRequests
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|Resource
name|capability
parameter_list|)
function_decl|;
comment|/**    * It returns the NMToken received on allocate call. It will not communicate    * with RM to get NMTokens. On allocate call whenever we receive new token    * along with container AMRMClient will cache this NMToken per node manager.    * This map returned should be shared with any application which is    * communicating with NodeManager (ex. NMClient) using NMTokens. If a new    * NMToken is received for the same node manager then it will be replaced.     */
DECL|method|getNMTokens ()
specifier|public
specifier|abstract
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|Token
argument_list|>
name|getNMTokens
parameter_list|()
function_decl|;
block|}
end_class

end_unit

