begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.async.impl
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
operator|.
name|impl
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
name|ArrayList
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
name|BlockingQueue
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
name|LinkedBlockingQueue
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
name|conf
operator|.
name|Configuration
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
name|api
operator|.
name|records
operator|.
name|UpdatedContainer
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
name|TimelineV2Client
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
name|AMRMClientAsync
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
name|ApplicationAttemptNotFoundException
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
name|exceptions
operator|.
name|YarnRuntimeException
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AMRMClientAsyncImpl
specifier|public
class|class
name|AMRMClientAsyncImpl
parameter_list|<
name|T
extends|extends
name|ContainerRequest
parameter_list|>
extends|extends
name|AMRMClientAsync
argument_list|<
name|T
argument_list|>
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
name|AMRMClientAsyncImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|heartbeatThread
specifier|private
specifier|final
name|HeartbeatThread
name|heartbeatThread
decl_stmt|;
DECL|field|handlerThread
specifier|private
specifier|final
name|CallbackHandlerThread
name|handlerThread
decl_stmt|;
DECL|field|responseQueue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|Object
argument_list|>
name|responseQueue
decl_stmt|;
DECL|field|unregisterHeartbeatLock
specifier|private
specifier|final
name|Object
name|unregisterHeartbeatLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|keepRunning
specifier|private
specifier|volatile
name|boolean
name|keepRunning
decl_stmt|;
DECL|field|progress
specifier|private
specifier|volatile
name|float
name|progress
decl_stmt|;
comment|/**    *    * @param intervalMs heartbeat interval in milliseconds between AM and RM    * @param callbackHandler callback handler that processes responses from    *                        the<code>ResourceManager</code>    */
DECL|method|AMRMClientAsyncImpl ( int intervalMs, AbstractCallbackHandler callbackHandler)
specifier|public
name|AMRMClientAsyncImpl
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
DECL|method|AMRMClientAsyncImpl (AMRMClient<T> client, int intervalMs, AbstractCallbackHandler callbackHandler)
specifier|public
name|AMRMClientAsyncImpl
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
name|client
argument_list|,
name|intervalMs
argument_list|,
name|callbackHandler
argument_list|)
expr_stmt|;
name|heartbeatThread
operator|=
operator|new
name|HeartbeatThread
argument_list|()
expr_stmt|;
name|handlerThread
operator|=
operator|new
name|CallbackHandlerThread
argument_list|()
expr_stmt|;
name|responseQueue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|keepRunning
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    *    * @deprecated Use {@link #AMRMClientAsyncImpl(int,    *             AMRMClientAsync.AbstractCallbackHandler)} instead.    */
annotation|@
name|Deprecated
DECL|method|AMRMClientAsyncImpl (int intervalMs, CallbackHandler callbackHandler)
specifier|public
name|AMRMClientAsyncImpl
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
DECL|method|AMRMClientAsyncImpl (AMRMClient<T> client, int intervalMs, CallbackHandler callbackHandler)
specifier|public
name|AMRMClientAsyncImpl
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
name|client
argument_list|,
name|intervalMs
argument_list|,
name|callbackHandler
argument_list|)
expr_stmt|;
name|heartbeatThread
operator|=
operator|new
name|HeartbeatThread
argument_list|()
expr_stmt|;
name|handlerThread
operator|=
operator|new
name|CallbackHandlerThread
argument_list|()
expr_stmt|;
name|responseQueue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|keepRunning
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|client
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|handlerThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|handlerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tells the heartbeat and handler threads to stop and waits for them to    * terminate.    */
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|keepRunning
operator|=
literal|false
expr_stmt|;
name|heartbeatThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|heartbeatThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error joining with heartbeat thread"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
name|handlerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
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
name|capability
argument_list|)
return|;
block|}
comment|/**    * Registers this application master with the resource manager. On successful    * registration, starts the heartbeating thread.    * @throws YarnException    * @throws IOException    */
DECL|method|registerApplicationMaster ( String appHostName, int appHostPort, String appTrackingUrl)
specifier|public
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
block|{
name|RegisterApplicationMasterResponse
name|response
init|=
name|client
operator|.
name|registerApplicationMaster
argument_list|(
name|appHostName
argument_list|,
name|appHostPort
argument_list|,
name|appTrackingUrl
argument_list|)
decl_stmt|;
name|heartbeatThread
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Unregister the application master. This must be called in the end.    * @param appStatus Success/Failure status of the master    * @param appMessage Diagnostics message on failure    * @param appTrackingUrl New URL to get master info    * @throws YarnException    * @throws IOException    */
DECL|method|unregisterApplicationMaster (FinalApplicationStatus appStatus, String appMessage, String appTrackingUrl)
specifier|public
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
block|{
synchronized|synchronized
init|(
name|unregisterHeartbeatLock
init|)
block|{
name|keepRunning
operator|=
literal|false
expr_stmt|;
name|client
operator|.
name|unregisterApplicationMaster
argument_list|(
name|appStatus
argument_list|,
name|appMessage
argument_list|,
name|appTrackingUrl
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Request containers for resources before calling<code>allocate</code>    * @param req Resource request    */
DECL|method|addContainerRequest (T req)
specifier|public
name|void
name|addContainerRequest
parameter_list|(
name|T
name|req
parameter_list|)
block|{
name|client
operator|.
name|addContainerRequest
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove previous container request. The previous container request may have     * already been sent to the ResourceManager. So even after the remove request     * the app must be prepared to receive an allocation for the previous request     * even after the remove request    * @param req Resource request    */
DECL|method|removeContainerRequest (T req)
specifier|public
name|void
name|removeContainerRequest
parameter_list|(
name|T
name|req
parameter_list|)
block|{
name|client
operator|.
name|removeContainerRequest
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|requestContainerUpdate (Container container, UpdateContainerRequest updateContainerRequest)
specifier|public
name|void
name|requestContainerUpdate
parameter_list|(
name|Container
name|container
parameter_list|,
name|UpdateContainerRequest
name|updateContainerRequest
parameter_list|)
block|{
name|client
operator|.
name|requestContainerUpdate
argument_list|(
name|container
argument_list|,
name|updateContainerRequest
argument_list|)
expr_stmt|;
block|}
comment|/**    * Release containers assigned by the Resource Manager. If the app cannot use    * the container or wants to give up the container then it can release them.    * The app needs to make new requests for the released resource capability if    * it still needs it. eg. it released non-local resources    * @param containerId    */
DECL|method|releaseAssignedContainer (ContainerId containerId)
specifier|public
name|void
name|releaseAssignedContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|client
operator|.
name|releaseAssignedContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the currently available resources in the cluster.    * A valid value is available after a call to allocate has been made    * @return Currently available resources    */
DECL|method|getAvailableResources ()
specifier|public
name|Resource
name|getAvailableResources
parameter_list|()
block|{
return|return
name|client
operator|.
name|getAvailableResources
argument_list|()
return|;
block|}
comment|/**    * Get the current number of nodes in the cluster.    * A valid values is available after a call to allocate has been made    * @return Current number of nodes in the cluster    */
DECL|method|getClusterNodeCount ()
specifier|public
name|int
name|getClusterNodeCount
parameter_list|()
block|{
return|return
name|client
operator|.
name|getClusterNodeCount
argument_list|()
return|;
block|}
comment|/**    * Update application's blacklist with addition or removal resources.    *    * @param blacklistAdditions list of resources which should be added to the    *        application blacklist    * @param blacklistRemovals list of resources which should be removed from the    *        application blacklist    */
DECL|method|updateBlacklist (List<String> blacklistAdditions, List<String> blacklistRemovals)
specifier|public
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
block|{
name|client
operator|.
name|updateBlacklist
argument_list|(
name|blacklistAdditions
argument_list|,
name|blacklistRemovals
argument_list|)
expr_stmt|;
block|}
DECL|class|HeartbeatThread
specifier|private
class|class
name|HeartbeatThread
extends|extends
name|Thread
block|{
DECL|method|HeartbeatThread ()
specifier|public
name|HeartbeatThread
parameter_list|()
block|{
name|super
argument_list|(
literal|"AMRM Heartbeater thread"
argument_list|)
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Object
name|response
init|=
literal|null
decl_stmt|;
comment|// synchronization ensures we don't send heartbeats after unregistering
synchronized|synchronized
init|(
name|unregisterHeartbeatLock
init|)
block|{
if|if
condition|(
operator|!
name|keepRunning
condition|)
block|{
return|return;
block|}
try|try
block|{
name|response
operator|=
name|client
operator|.
name|allocate
argument_list|(
name|progress
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ApplicationAttemptNotFoundException
name|e
parameter_list|)
block|{
name|handler
operator|.
name|onShutdownRequest
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutdown requested. Stopping callback."
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception on heartbeat"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|response
operator|=
name|ex
expr_stmt|;
block|}
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|responseQueue
operator|.
name|put
argument_list|(
name|response
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Interrupted while waiting to put on response queue"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|heartbeatIntervalMs
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Heartbeater interrupted"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|CallbackHandlerThread
specifier|private
class|class
name|CallbackHandlerThread
extends|extends
name|Thread
block|{
DECL|method|CallbackHandlerThread ()
specifier|public
name|CallbackHandlerThread
parameter_list|()
block|{
name|super
argument_list|(
literal|"AMRM Callback Handler Thread"
argument_list|)
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|keepRunning
condition|)
block|{
return|return;
block|}
try|try
block|{
name|Object
name|object
decl_stmt|;
try|try
block|{
name|object
operator|=
name|responseQueue
operator|.
name|take
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Interrupted while waiting for queue"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|object
operator|instanceof
name|Throwable
condition|)
block|{
name|progress
operator|=
name|handler
operator|.
name|getProgress
argument_list|()
expr_stmt|;
name|handler
operator|.
name|onError
argument_list|(
operator|(
name|Throwable
operator|)
name|object
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|AllocateResponse
name|response
init|=
operator|(
name|AllocateResponse
operator|)
name|object
decl_stmt|;
name|String
name|collectorAddress
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getCollectorInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|collectorAddress
operator|=
name|response
operator|.
name|getCollectorInfo
argument_list|()
operator|.
name|getCollectorAddr
argument_list|()
expr_stmt|;
block|}
name|TimelineV2Client
name|timelineClient
init|=
name|client
operator|.
name|getRegisteredTimelineV2Client
argument_list|()
decl_stmt|;
if|if
condition|(
name|timelineClient
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getCollectorInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|timelineClient
operator|.
name|setTimelineCollectorInfo
argument_list|(
name|response
operator|.
name|getCollectorInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
init|=
name|response
operator|.
name|getUpdatedNodes
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|updatedNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|handler
operator|.
name|onNodesUpdated
argument_list|(
name|updatedNodes
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completed
init|=
name|response
operator|.
name|getCompletedContainersStatuses
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|completed
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|handler
operator|.
name|onContainersCompleted
argument_list|(
name|completed
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|handler
operator|instanceof
name|AMRMClientAsync
operator|.
name|AbstractCallbackHandler
condition|)
block|{
comment|// RM side of the implementation guarantees that there are
comment|// no duplications between increased and decreased containers
name|List
argument_list|<
name|UpdatedContainer
argument_list|>
name|changed
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|changed
operator|.
name|addAll
argument_list|(
name|response
operator|.
name|getUpdatedContainers
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|changed
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
operator|(
operator|(
name|AMRMClientAsync
operator|.
name|AbstractCallbackHandler
operator|)
name|handler
operator|)
operator|.
name|onContainersUpdated
argument_list|(
name|changed
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Container
argument_list|>
name|allocated
init|=
name|response
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|allocated
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|handler
operator|.
name|onContainersAllocated
argument_list|(
name|allocated
argument_list|)
expr_stmt|;
block|}
name|progress
operator|=
name|handler
operator|.
name|getProgress
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|handler
operator|.
name|onError
argument_list|(
name|ex
argument_list|)
expr_stmt|;
comment|// re-throw exception to end the thread
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

