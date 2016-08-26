begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.async
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
operator|.
name|async
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
name|base
operator|.
name|Preconditions
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
name|base
operator|.
name|Supplier
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
name|atomic
operator|.
name|AtomicInteger
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|client
operator|.
name|api
operator|.
name|AMRMClient
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
name|AMRMClient
operator|.
name|ContainerRequest
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
name|async
operator|.
name|impl
operator|.
name|AMRMClientAsyncImpl
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
name|client
operator|.
name|api
operator|.
name|TimelineClient
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  *<code>AMRMClientAsync</code> handles communication with the ResourceManager  * and provides asynchronous updates on events such as container allocations and  * completions.  It contains a thread that sends periodic heartbeats to the  * ResourceManager.  *   * It should be used by implementing a CallbackHandler:  *<pre>  * {@code  * class MyCallbackHandler extends AMRMClientAsync.AbstractCallbackHandler {  *   public void onContainersAllocated(List<Container> containers) {  *     [run tasks on the containers]  *   }  *  *   public void onContainersResourceChanged(List<Container> containers) {  *     [determine if resource allocation of containers have been increased in  *      the ResourceManager, and if so, inform the NodeManagers to increase the  *      resource monitor/enforcement on the containers]  *   }  *  *   public void onContainersCompleted(List<ContainerStatus> statuses) {  *     [update progress, check whether app is done]  *   }  *     *   public void onNodesUpdated(List<NodeReport> updated) {}  *     *   public void onReboot() {}  * }  * }  *</pre>  *   * The client's lifecycle should be managed similarly to the following:  *   *<pre>  * {@code  * AMRMClientAsync asyncClient =   *     createAMRMClientAsync(appAttId, 1000, new MyCallbackhandler());  * asyncClient.init(conf);  * asyncClient.start();  * RegisterApplicationMasterResponse response = asyncClient  *    .registerApplicationMaster(appMasterHostname, appMasterRpcPort,  *       appMasterTrackingUrl);  * asyncClient.addContainerRequest(containerRequest);  * [... wait for application to complete]  * asyncClient.unregisterApplicationMaster(status, appMsg, trackingUrl);  * asyncClient.stop();  * }  *</pre>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|AMRMClientAsync
specifier|public
specifier|abstract
class|class
name|AMRMClientAsync
parameter_list|<
name|T
extends|extends
name|ContainerRequest
parameter_list|>
extends|extends
name|AbstractService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AMRMClientAsync
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|client
specifier|protected
specifier|final
name|AMRMClient
argument_list|<
name|T
argument_list|>
name|client
decl_stmt|;
DECL|field|handler
specifier|protected
specifier|final
name|CallbackHandler
name|handler
decl_stmt|;
DECL|field|heartbeatIntervalMs
specifier|protected
specifier|final
name|AtomicInteger
name|heartbeatIntervalMs
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/**    *<p>Create a new instance of AMRMClientAsync.</p>    *    * @param intervalMs heartbeat interval in milliseconds between AM and RM    * @param callbackHandler callback handler that processes responses from    *                        the<code>ResourceManager</code>    */
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|ContainerRequest
parameter_list|>
name|AMRMClientAsync
argument_list|<
name|T
argument_list|>
DECL|method|createAMRMClientAsync ( int intervalMs, AbstractCallbackHandler callbackHandler)
name|createAMRMClientAsync
parameter_list|(
name|int
name|intervalMs
parameter_list|,
name|AbstractCallbackHandler
name|callbackHandler
parameter_list|)
block|{
return|return
operator|new
name|AMRMClientAsyncImpl
argument_list|<
name|T
argument_list|>
argument_list|(
name|intervalMs
argument_list|,
name|callbackHandler
argument_list|)
return|;
block|}
comment|/**    *<p>Create a new instance of AMRMClientAsync.</p>    *    * @param client the AMRMClient instance    * @param intervalMs heartbeat interval in milliseconds between AM and RM    * @param callbackHandler callback handler that processes responses from    *                        the<code>ResourceManager</code>    */
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|ContainerRequest
parameter_list|>
name|AMRMClientAsync
argument_list|<
name|T
argument_list|>
DECL|method|createAMRMClientAsync ( AMRMClient<T> client, int intervalMs, AbstractCallbackHandler callbackHandler)
name|createAMRMClientAsync
parameter_list|(
name|AMRMClient
argument_list|<
name|T
argument_list|>
name|client
parameter_list|,
name|int
name|intervalMs
parameter_list|,
name|AbstractCallbackHandler
name|callbackHandler
parameter_list|)
block|{
return|return
operator|new
name|AMRMClientAsyncImpl
argument_list|<
name|T
argument_list|>
argument_list|(
name|client
argument_list|,
name|intervalMs
argument_list|,
name|callbackHandler
argument_list|)
return|;
block|}
DECL|method|AMRMClientAsync ( int intervalMs, AbstractCallbackHandler callbackHandler)
specifier|protected
name|AMRMClientAsync
parameter_list|(
name|int
name|intervalMs
parameter_list|,
name|AbstractCallbackHandler
name|callbackHandler
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|AMRMClientImpl
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|,
name|intervalMs
argument_list|,
name|callbackHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|AMRMClientAsync (AMRMClient<T> client, int intervalMs, AbstractCallbackHandler callbackHandler)
specifier|protected
name|AMRMClientAsync
parameter_list|(
name|AMRMClient
argument_list|<
name|T
argument_list|>
name|client
parameter_list|,
name|int
name|intervalMs
parameter_list|,
name|AbstractCallbackHandler
name|callbackHandler
parameter_list|)
block|{
name|super
argument_list|(
name|AMRMClientAsync
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|heartbeatIntervalMs
operator|.
name|set
argument_list|(
name|intervalMs
argument_list|)
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|callbackHandler
expr_stmt|;
block|}
comment|/**    *    * @deprecated Use {@link #createAMRMClientAsync(int,    *             AMRMClientAsync.AbstractCallbackHandler)} instead.    */
annotation|@
name|Deprecated
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|ContainerRequest
parameter_list|>
name|AMRMClientAsync
argument_list|<
name|T
argument_list|>
DECL|method|createAMRMClientAsync (int intervalMs, CallbackHandler callbackHandler)
name|createAMRMClientAsync
parameter_list|(
name|int
name|intervalMs
parameter_list|,
name|CallbackHandler
name|callbackHandler
parameter_list|)
block|{
return|return
operator|new
name|AMRMClientAsyncImpl
argument_list|<
name|T
argument_list|>
argument_list|(
name|intervalMs
argument_list|,
name|callbackHandler
argument_list|)
return|;
block|}
comment|/**    *    * @deprecated Use {@link #createAMRMClientAsync(AMRMClient,    *             int, AMRMClientAsync.AbstractCallbackHandler)} instead.    */
annotation|@
name|Deprecated
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|ContainerRequest
parameter_list|>
name|AMRMClientAsync
argument_list|<
name|T
argument_list|>
DECL|method|createAMRMClientAsync (AMRMClient<T> client, int intervalMs, CallbackHandler callbackHandler)
name|createAMRMClientAsync
parameter_list|(
name|AMRMClient
argument_list|<
name|T
argument_list|>
name|client
parameter_list|,
name|int
name|intervalMs
parameter_list|,
name|CallbackHandler
name|callbackHandler
parameter_list|)
block|{
return|return
operator|new
name|AMRMClientAsyncImpl
argument_list|<
name|T
argument_list|>
argument_list|(
name|client
argument_list|,
name|intervalMs
argument_list|,
name|callbackHandler
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
DECL|method|AMRMClientAsync (int intervalMs, CallbackHandler callbackHandler)
specifier|protected
name|AMRMClientAsync
parameter_list|(
name|int
name|intervalMs
parameter_list|,
name|CallbackHandler
name|callbackHandler
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|AMRMClientImpl
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|,
name|intervalMs
argument_list|,
name|callbackHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
annotation|@
name|Deprecated
DECL|method|AMRMClientAsync (AMRMClient<T> client, int intervalMs, CallbackHandler callbackHandler)
specifier|protected
name|AMRMClientAsync
parameter_list|(
name|AMRMClient
argument_list|<
name|T
argument_list|>
name|client
parameter_list|,
name|int
name|intervalMs
parameter_list|,
name|CallbackHandler
name|callbackHandler
parameter_list|)
block|{
name|super
argument_list|(
name|AMRMClientAsync
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|heartbeatIntervalMs
operator|.
name|set
argument_list|(
name|intervalMs
argument_list|)
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|callbackHandler
expr_stmt|;
block|}
DECL|method|setHeartbeatInterval (int interval)
specifier|public
name|void
name|setHeartbeatInterval
parameter_list|(
name|int
name|interval
parameter_list|)
block|{
name|heartbeatIntervalMs
operator|.
name|set
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Returns all matching ContainerRequests that match the given Priority,    * ResourceName, ExecutionType and Capability.    *    * NOTE: This matches only requests that were made by the client WITHOUT the    * allocationRequestId specified.    *    * @param priority Priority.    * @param resourceName Location.    * @param executionType ExecutionType.    * @param capability Capability.    * @return All matching ContainerRequests    */
DECL|method|getMatchingRequests ( Priority priority, String resourceName, ExecutionType executionType, Resource capability)
specifier|public
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
name|ExecutionType
name|executionType
parameter_list|,
name|Resource
name|capability
parameter_list|)
block|{
return|return
name|client
operator|.
name|getMatchingRequests
argument_list|(
name|priority
argument_list|,
name|resourceName
argument_list|,
name|executionType
argument_list|,
name|capability
argument_list|)
return|;
block|}
comment|/**    * Returns all matching ContainerRequests that match the given    * AllocationRequestId.    *    * NOTE: This matches only requests that were made by the client WITH the    * allocationRequestId specified.    *    * @param allocationRequestId AllocationRequestId.    * @return All matching ContainerRequests    */
DECL|method|getMatchingRequests (long allocationRequestId)
specifier|public
name|Collection
argument_list|<
name|T
argument_list|>
name|getMatchingRequests
parameter_list|(
name|long
name|allocationRequestId
parameter_list|)
block|{
return|return
name|client
operator|.
name|getMatchingRequests
argument_list|(
name|allocationRequestId
argument_list|)
return|;
block|}
comment|/**    * Registers this application master with the resource manager. On successful    * registration, starts the heartbeating thread.    * @throws YarnException    * @throws IOException    */
DECL|method|registerApplicationMaster ( String appHostName, int appHostPort, String appTrackingUrl)
specifier|public
specifier|abstract
name|RegisterApplicationMasterResponse
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
comment|/**    * Unregister the application master. This must be called in the end.    * @param appStatus Success/Failure status of the master    * @param appMessage Diagnostics message on failure    * @param appTrackingUrl New URL to get master info    * @throws YarnException    * @throws IOException    */
DECL|method|unregisterApplicationMaster ( FinalApplicationStatus appStatus, String appMessage, String appTrackingUrl)
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
comment|/**    * Request container resource change before calling<code>allocate</code>.    * Any previous pending resource change request of the same container will be    * removed.    *    * Application that calls this method is expected to maintain the    *<code>Container</code>s that are returned from previous successful    * allocations or resource changes. By passing in the existing container and a    * target resource capability to this method, the application requests the    * ResourceManager to change the existing resource allocation to the target    * resource allocation.    *    * @param container The container returned from the last successful resource    *                  allocation or resource change    * @param capability  The target resource capability of the container    */
DECL|method|requestContainerResourceChange ( Container container, Resource capability)
specifier|public
specifier|abstract
name|void
name|requestContainerResourceChange
parameter_list|(
name|Container
name|container
parameter_list|,
name|Resource
name|capability
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
comment|/**    * Register TimelineClient to AMRMClient.    * @param timelineClient    */
DECL|method|registerTimelineClient (TimelineClient timelineClient)
specifier|public
name|void
name|registerTimelineClient
parameter_list|(
name|TimelineClient
name|timelineClient
parameter_list|)
block|{
name|client
operator|.
name|registerTimelineClient
argument_list|(
name|timelineClient
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get registered timeline client.    * @return the registered timeline client    */
DECL|method|getRegisteredTimelineClient ()
specifier|public
name|TimelineClient
name|getRegisteredTimelineClient
parameter_list|()
block|{
return|return
name|client
operator|.
name|getRegisteredTimelineClient
argument_list|()
return|;
block|}
comment|/**    * Update application's blacklist with addition or removal resources.    *    * @param blacklistAdditions list of resources which should be added to the    *        application blacklist    * @param blacklistRemovals list of resources which should be removed from the    *        application blacklist    */
DECL|method|updateBlacklist (List<String> blacklistAdditions, List<String> blacklistRemovals)
specifier|public
specifier|abstract
name|void
name|updateBlacklist
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|blacklistAdditions
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|blacklistRemovals
parameter_list|)
function_decl|;
comment|/**    * Wait for<code>check</code> to return true for each 1000 ms.    * See also {@link #waitFor(com.google.common.base.Supplier, int)}    * and {@link #waitFor(com.google.common.base.Supplier, int, int)}    * @param check the condition for which it should wait    */
DECL|method|waitFor (Supplier<Boolean> check)
specifier|public
name|void
name|waitFor
parameter_list|(
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|check
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|waitFor
argument_list|(
name|check
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Wait for<code>check</code> to return true for each    *<code>checkEveryMillis</code> ms.    * See also {@link #waitFor(com.google.common.base.Supplier, int, int)}    * @param check user defined checker    * @param checkEveryMillis interval to call<code>check</code>    */
DECL|method|waitFor (Supplier<Boolean> check, int checkEveryMillis)
specifier|public
name|void
name|waitFor
parameter_list|(
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|check
parameter_list|,
name|int
name|checkEveryMillis
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|waitFor
argument_list|(
name|check
argument_list|,
name|checkEveryMillis
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
comment|/**    * Wait for<code>check</code> to return true for each    *<code>checkEveryMillis</code> ms. In the main loop, this method will log    * the message "waiting in main loop" for each<code>logInterval</code> times    * iteration to confirm the thread is alive.    * @param check user defined checker    * @param checkEveryMillis interval to call<code>check</code>    * @param logInterval interval to log for each    */
DECL|method|waitFor (Supplier<Boolean> check, int checkEveryMillis, int logInterval)
specifier|public
name|void
name|waitFor
parameter_list|(
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|check
parameter_list|,
name|int
name|checkEveryMillis
parameter_list|,
name|int
name|logInterval
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|check
argument_list|,
literal|"check should not be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|checkEveryMillis
operator|>=
literal|0
argument_list|,
literal|"checkEveryMillis should be positive value"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|logInterval
operator|>=
literal|0
argument_list|,
literal|"logInterval should be positive value"
argument_list|)
expr_stmt|;
name|int
name|loggingCounter
init|=
name|logInterval
decl_stmt|;
do|do
block|{
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
literal|"Check the condition for main loop."
argument_list|)
expr_stmt|;
block|}
name|boolean
name|result
init|=
name|check
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exits the main loop."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|--
name|loggingCounter
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting in main loop."
argument_list|)
expr_stmt|;
name|loggingCounter
operator|=
name|logInterval
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|checkEveryMillis
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
comment|/**    *<p>    * The callback abstract class. The callback functions need to be implemented    * by {@link AMRMClientAsync} users. The APIs are called when responses from    * the<code>ResourceManager</code> are available.    *</p>    */
DECL|class|AbstractCallbackHandler
specifier|public
specifier|abstract
specifier|static
class|class
name|AbstractCallbackHandler
implements|implements
name|CallbackHandler
block|{
comment|/**      * Called when the ResourceManager responds to a heartbeat with completed      * containers. If the response contains both completed containers and      * allocated containers, this will be called before containersAllocated.      */
DECL|method|onContainersCompleted (List<ContainerStatus> statuses)
specifier|public
specifier|abstract
name|void
name|onContainersCompleted
parameter_list|(
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|statuses
parameter_list|)
function_decl|;
comment|/**      * Called when the ResourceManager responds to a heartbeat with allocated      * containers. If the response containers both completed containers and      * allocated containers, this will be called after containersCompleted.      */
DECL|method|onContainersAllocated (List<Container> containers)
specifier|public
specifier|abstract
name|void
name|onContainersAllocated
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
function_decl|;
comment|/**      * Called when the ResourceManager responds to a heartbeat with containers      * whose resource allocation has been changed.      */
DECL|method|onContainersResourceChanged ( List<Container> containers)
specifier|public
specifier|abstract
name|void
name|onContainersResourceChanged
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
function_decl|;
comment|/**      * Called when the ResourceManager wants the ApplicationMaster to shutdown      * for being out of sync etc. The ApplicationMaster should not unregister      * with the RM unless the ApplicationMaster wants to be the last attempt.      */
DECL|method|onShutdownRequest ()
specifier|public
specifier|abstract
name|void
name|onShutdownRequest
parameter_list|()
function_decl|;
comment|/**      * Called when nodes tracked by the ResourceManager have changed in health,      * availability etc.      */
DECL|method|onNodesUpdated (List<NodeReport> updatedNodes)
specifier|public
specifier|abstract
name|void
name|onNodesUpdated
parameter_list|(
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|)
function_decl|;
DECL|method|getProgress ()
specifier|public
specifier|abstract
name|float
name|getProgress
parameter_list|()
function_decl|;
comment|/**      * Called when error comes from RM communications as well as from errors in      * the callback itself from the app. Calling      * stop() is the recommended action.      */
DECL|method|onError (Throwable e)
specifier|public
specifier|abstract
name|void
name|onError
parameter_list|(
name|Throwable
name|e
parameter_list|)
function_decl|;
block|}
comment|/**    * @deprecated Use {@link AMRMClientAsync.AbstractCallbackHandler} instead.    */
annotation|@
name|Deprecated
DECL|interface|CallbackHandler
specifier|public
interface|interface
name|CallbackHandler
block|{
comment|/**      * Called when the ResourceManager responds to a heartbeat with completed      * containers. If the response contains both completed containers and      * allocated containers, this will be called before containersAllocated.      */
DECL|method|onContainersCompleted (List<ContainerStatus> statuses)
name|void
name|onContainersCompleted
parameter_list|(
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|statuses
parameter_list|)
function_decl|;
comment|/**      * Called when the ResourceManager responds to a heartbeat with allocated      * containers. If the response containers both completed containers and      * allocated containers, this will be called after containersCompleted.      */
DECL|method|onContainersAllocated (List<Container> containers)
name|void
name|onContainersAllocated
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
function_decl|;
comment|/**      * Called when the ResourceManager wants the ApplicationMaster to shutdown      * for being out of sync etc. The ApplicationMaster should not unregister      * with the RM unless the ApplicationMaster wants to be the last attempt.      */
DECL|method|onShutdownRequest ()
name|void
name|onShutdownRequest
parameter_list|()
function_decl|;
comment|/**      * Called when nodes tracked by the ResourceManager have changed in health,      * availability etc.      */
DECL|method|onNodesUpdated (List<NodeReport> updatedNodes)
name|void
name|onNodesUpdated
parameter_list|(
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|)
function_decl|;
DECL|method|getProgress ()
name|float
name|getProgress
parameter_list|()
function_decl|;
comment|/**      * Called when error comes from RM communications as well as from errors in      * the callback itself from the app. Calling      * stop() is the recommended action.      *      * @param e      */
DECL|method|onError (Throwable e)
name|void
name|onError
parameter_list|(
name|Throwable
name|e
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

