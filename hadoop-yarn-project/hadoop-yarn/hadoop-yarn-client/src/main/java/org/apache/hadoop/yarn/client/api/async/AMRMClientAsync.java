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
comment|/**  *<code>AMRMClientAsync</code> handles communication with the ResourceManager  * and provides asynchronous updates on events such as container allocations and  * completions.  It contains a thread that sends periodic heartbeats to the  * ResourceManager.  *   * It should be used by implementing a CallbackHandler:  *<pre>  * {@code  * class MyCallbackHandler implements AMRMClientAsync.CallbackHandler {  *   public void onContainersAllocated(List<Container> containers) {  *     [run tasks on the containers]  *   }  *     *   public void onContainersCompleted(List<ContainerStatus> statuses) {  *     [update progress, check whether app is done]  *   }  *     *   public void onNodesUpdated(List<NodeReport> updated) {}  *     *   public void onReboot() {}  * }  * }  *</pre>  *   * The client's lifecycle should be managed similarly to the following:  *   *<pre>  * {@code  * AMRMClientAsync asyncClient =   *     createAMRMClientAsync(appAttId, 1000, new MyCallbackhandler());  * asyncClient.init(conf);  * asyncClient.start();  * RegisterApplicationMasterResponse response = asyncClient  *    .registerApplicationMaster(appMasterHostname, appMasterRpcPort,  *       appMasterTrackingUrl);  * asyncClient.addContainerRequest(containerRequest);  * [... wait for application to complete]  * asyncClient.unregisterApplicationMaster(status, appMsg, trackingUrl);  * asyncClient.stop();  * }  *</pre>  */
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
DECL|method|createAMRMClientAsync ( ApplicationAttemptId id, int intervalMs, CallbackHandler callbackHandler)
name|createAMRMClientAsync
parameter_list|(
name|ApplicationAttemptId
name|id
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
name|id
argument_list|,
name|intervalMs
argument_list|,
name|callbackHandler
argument_list|)
return|;
block|}
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
DECL|method|createAMRMClientAsync ( AMRMClient<T> client, int intervalMs, CallbackHandler callbackHandler)
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
DECL|method|AMRMClientAsync (ApplicationAttemptId id, int intervalMs, CallbackHandler callbackHandler)
specifier|protected
name|AMRMClientAsync
parameter_list|(
name|ApplicationAttemptId
name|id
parameter_list|,
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
argument_list|(
name|id
argument_list|)
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
comment|/**    * It returns the NMToken received on allocate call. It will not communicate    * with RM to get NMTokens. On allocate call whenever we receive new token    * along with new container AMRMClientAsync will cache this NMToken per node    * manager. This map returned should be shared with any application which is    * communicating with NodeManager (ex. NMClient / NMClientAsync) using    * NMTokens. If a new NMToken is received for the same node manager    * then it will be replaced.     */
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
DECL|interface|CallbackHandler
specifier|public
interface|interface
name|CallbackHandler
block|{
comment|/**      * Called when the ResourceManager responds to a heartbeat with completed      * containers. If the response contains both completed containers and      * allocated containers, this will be called before containersAllocated.      */
DECL|method|onContainersCompleted (List<ContainerStatus> statuses)
specifier|public
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
specifier|public
name|void
name|onShutdownRequest
parameter_list|()
function_decl|;
comment|/**      * Called when nodes tracked by the ResourceManager have changed in health,      * availability etc.      */
DECL|method|onNodesUpdated (List<NodeReport> updatedNodes)
specifier|public
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
name|float
name|getProgress
parameter_list|()
function_decl|;
DECL|method|onError (Exception e)
specifier|public
name|void
name|onError
parameter_list|(
name|Exception
name|e
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

