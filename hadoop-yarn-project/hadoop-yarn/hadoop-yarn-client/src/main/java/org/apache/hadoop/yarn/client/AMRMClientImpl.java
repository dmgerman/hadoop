begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|ipc
operator|.
name|RPC
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
name|security
operator|.
name|UserGroupInformation
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
name|conf
operator|.
name|YarnConfiguration
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
name|YarnRemoteException
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
name|ipc
operator|.
name|YarnRPC
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
name|util
operator|.
name|BuilderUtils
import|;
end_import

begin_class
annotation|@
name|Unstable
DECL|class|AMRMClientImpl
specifier|public
class|class
name|AMRMClientImpl
extends|extends
name|AbstractService
implements|implements
name|AMRMClient
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
name|AMRMClientImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|lastResponseId
specifier|private
name|int
name|lastResponseId
init|=
literal|0
decl_stmt|;
DECL|field|rmClient
specifier|protected
name|AMRMProtocol
name|rmClient
decl_stmt|;
DECL|field|appAttemptId
specifier|protected
specifier|final
name|ApplicationAttemptId
name|appAttemptId
decl_stmt|;
DECL|field|clusterAvailableResources
specifier|protected
name|Resource
name|clusterAvailableResources
decl_stmt|;
DECL|field|clusterNodeCount
specifier|protected
name|int
name|clusterNodeCount
decl_stmt|;
comment|//Key -> Priority
comment|//Value -> Map
comment|//Key->ResourceName (e.g., hostname, rackname, *)
comment|//Value->Map
comment|//Key->Resource Capability
comment|//Value->ResourceRequest
specifier|protected
specifier|final
name|Map
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
argument_list|>
DECL|field|remoteRequestsTable
name|remoteRequestsTable
init|=
operator|new
name|TreeMap
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|ask
specifier|protected
specifier|final
name|Set
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
init|=
operator|new
name|TreeSet
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|(
operator|new
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
name|BuilderUtils
operator|.
name|ResourceRequestComparator
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|release
specifier|protected
specifier|final
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|release
init|=
operator|new
name|TreeSet
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|AMRMClientImpl (ApplicationAttemptId appAttemptId)
specifier|public
name|AMRMClientImpl
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
block|{
name|super
argument_list|(
name|AMRMClientImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|appAttemptId
operator|=
name|appAttemptId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
specifier|final
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|rmAddress
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_PORT
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|currentUser
decl_stmt|;
try|try
block|{
name|currentUser
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// CurrentUser should already have AMToken loaded.
name|rmClient
operator|=
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|AMRMProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AMRMProtocol
name|run
parameter_list|()
block|{
return|return
operator|(
name|AMRMProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|AMRMProtocol
operator|.
name|class
argument_list|,
name|rmAddress
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to ResourceManager at "
operator|+
name|rmAddress
argument_list|)
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|rmClient
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|this
operator|.
name|rmClient
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|YarnRemoteException
block|{
comment|// do this only once ???
name|RegisterApplicationMasterRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|request
operator|.
name|setApplicationAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
block|}
name|request
operator|.
name|setHost
argument_list|(
name|appHostName
argument_list|)
expr_stmt|;
name|request
operator|.
name|setRpcPort
argument_list|(
name|appHostPort
argument_list|)
expr_stmt|;
if|if
condition|(
name|appTrackingUrl
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|setTrackingUrl
argument_list|(
name|appTrackingUrl
argument_list|)
expr_stmt|;
block|}
name|RegisterApplicationMasterResponse
name|response
init|=
name|rmClient
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|allocate (float progressIndicator)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|float
name|progressIndicator
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|AllocateResponse
name|allocateResponse
init|=
literal|null
decl_stmt|;
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
name|askList
init|=
literal|null
decl_stmt|;
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
name|releaseList
init|=
literal|null
decl_stmt|;
name|AllocateRequest
name|allocateRequest
init|=
literal|null
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|askList
operator|=
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|(
name|ask
argument_list|)
expr_stmt|;
name|releaseList
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|(
name|release
argument_list|)
expr_stmt|;
comment|// optimistically clear this collection assuming no RPC failure
name|ask
operator|.
name|clear
argument_list|()
expr_stmt|;
name|release
operator|.
name|clear
argument_list|()
expr_stmt|;
name|allocateRequest
operator|=
name|BuilderUtils
operator|.
name|newAllocateRequest
argument_list|(
name|appAttemptId
argument_list|,
name|lastResponseId
argument_list|,
name|progressIndicator
argument_list|,
name|askList
argument_list|,
name|releaseList
argument_list|)
expr_stmt|;
block|}
name|allocateResponse
operator|=
name|rmClient
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// update these on successful RPC
name|clusterNodeCount
operator|=
name|allocateResponse
operator|.
name|getNumClusterNodes
argument_list|()
expr_stmt|;
name|lastResponseId
operator|=
name|allocateResponse
operator|.
name|getResponseId
argument_list|()
expr_stmt|;
name|clusterAvailableResources
operator|=
name|allocateResponse
operator|.
name|getAvailableResources
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// TODO how to differentiate remote yarn exception vs error in rpc
if|if
condition|(
name|allocateResponse
operator|==
literal|null
condition|)
block|{
comment|// we hit an exception in allocate()
comment|// preserve ask and release for next call to allocate()
synchronized|synchronized
init|(
name|this
init|)
block|{
name|release
operator|.
name|addAll
argument_list|(
name|releaseList
argument_list|)
expr_stmt|;
comment|// requests could have been added or deleted during call to allocate
comment|// If requests were added/removed then there is nothing to do since
comment|// the ResourceRequest object in ask would have the actual new value.
comment|// If ask does not have this ResourceRequest then it was unchanged and
comment|// so we can add the value back safely.
comment|// This assumes that there will no concurrent calls to allocate() and
comment|// so we dont have to worry about ask being changed in the
comment|// synchronized block at the beginning of this method.
for|for
control|(
name|ResourceRequest
name|oldAsk
range|:
name|askList
control|)
block|{
if|if
condition|(
operator|!
name|ask
operator|.
name|contains
argument_list|(
name|oldAsk
argument_list|)
condition|)
block|{
name|ask
operator|.
name|add
argument_list|(
name|oldAsk
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|allocateResponse
return|;
block|}
annotation|@
name|Override
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
name|YarnRemoteException
block|{
name|FinishApplicationMasterRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setAppAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setFinishApplicationStatus
argument_list|(
name|appStatus
argument_list|)
expr_stmt|;
if|if
condition|(
name|appMessage
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|setDiagnostics
argument_list|(
name|appMessage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|appTrackingUrl
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|setTrackingUrl
argument_list|(
name|appTrackingUrl
argument_list|)
expr_stmt|;
block|}
name|rmClient
operator|.
name|finishApplicationMaster
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addContainerRequest (ContainerRequest req)
specifier|public
specifier|synchronized
name|void
name|addContainerRequest
parameter_list|(
name|ContainerRequest
name|req
parameter_list|)
block|{
comment|// Create resource requests
if|if
condition|(
name|req
operator|.
name|hosts
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|host
range|:
name|req
operator|.
name|hosts
control|)
block|{
name|addResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|host
argument_list|,
name|req
operator|.
name|capability
argument_list|,
name|req
operator|.
name|containerCount
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|req
operator|.
name|racks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|rack
range|:
name|req
operator|.
name|racks
control|)
block|{
name|addResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|rack
argument_list|,
name|req
operator|.
name|capability
argument_list|,
name|req
operator|.
name|containerCount
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Off-switch
name|addResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|req
operator|.
name|capability
argument_list|,
name|req
operator|.
name|containerCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeContainerRequest (ContainerRequest req)
specifier|public
specifier|synchronized
name|void
name|removeContainerRequest
parameter_list|(
name|ContainerRequest
name|req
parameter_list|)
block|{
comment|// Update resource requests
if|if
condition|(
name|req
operator|.
name|hosts
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|hostName
range|:
name|req
operator|.
name|hosts
control|)
block|{
name|decResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|hostName
argument_list|,
name|req
operator|.
name|capability
argument_list|,
name|req
operator|.
name|containerCount
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|req
operator|.
name|racks
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|rack
range|:
name|req
operator|.
name|racks
control|)
block|{
name|decResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|rack
argument_list|,
name|req
operator|.
name|capability
argument_list|,
name|req
operator|.
name|containerCount
argument_list|)
expr_stmt|;
block|}
block|}
name|decResourceRequest
argument_list|(
name|req
operator|.
name|priority
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|req
operator|.
name|capability
argument_list|,
name|req
operator|.
name|containerCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|releaseAssignedContainer (ContainerId containerId)
specifier|public
specifier|synchronized
name|void
name|releaseAssignedContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|release
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getClusterAvailableResources ()
specifier|public
specifier|synchronized
name|Resource
name|getClusterAvailableResources
parameter_list|()
block|{
return|return
name|clusterAvailableResources
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterNodeCount ()
specifier|public
specifier|synchronized
name|int
name|getClusterNodeCount
parameter_list|()
block|{
return|return
name|clusterNodeCount
return|;
block|}
DECL|method|addResourceRequestToAsk (ResourceRequest remoteRequest)
specifier|private
name|void
name|addResourceRequestToAsk
parameter_list|(
name|ResourceRequest
name|remoteRequest
parameter_list|)
block|{
comment|// This code looks weird but is needed because of the following scenario.
comment|// A ResourceRequest is removed from the remoteRequestTable. A 0 container
comment|// request is added to 'ask' to notify the RM about not needing it any more.
comment|// Before the call to allocate, the user now requests more containers. If
comment|// the locations of the 0 size request and the new request are the same
comment|// (with the difference being only container count), then the set comparator
comment|// will consider both to be the same and not add the new request to ask. So
comment|// we need to check for the "same" request being present and remove it and
comment|// then add it back. The comparator is container count agnostic.
comment|// This should happen only rarely but we do need to guard against it.
if|if
condition|(
name|ask
operator|.
name|contains
argument_list|(
name|remoteRequest
argument_list|)
condition|)
block|{
name|ask
operator|.
name|remove
argument_list|(
name|remoteRequest
argument_list|)
expr_stmt|;
block|}
name|ask
operator|.
name|add
argument_list|(
name|remoteRequest
argument_list|)
expr_stmt|;
block|}
DECL|method|addResourceRequest (Priority priority, String resourceName, Resource capability, int containerCount)
specifier|private
name|void
name|addResourceRequest
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|int
name|containerCount
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
name|remoteRequests
init|=
name|this
operator|.
name|remoteRequestsTable
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteRequests
operator|==
literal|null
condition|)
block|{
name|remoteRequests
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|remoteRequestsTable
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|remoteRequests
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
literal|"Added priority="
operator|+
name|priority
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
name|reqMap
init|=
name|remoteRequests
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqMap
operator|==
literal|null
condition|)
block|{
name|reqMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|()
expr_stmt|;
name|remoteRequests
operator|.
name|put
argument_list|(
name|resourceName
argument_list|,
name|reqMap
argument_list|)
expr_stmt|;
block|}
name|ResourceRequest
name|remoteRequest
init|=
name|reqMap
operator|.
name|get
argument_list|(
name|capability
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteRequest
operator|==
literal|null
condition|)
block|{
name|remoteRequest
operator|=
name|BuilderUtils
operator|.
name|newResourceRequest
argument_list|(
name|priority
argument_list|,
name|resourceName
argument_list|,
name|capability
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|reqMap
operator|.
name|put
argument_list|(
name|capability
argument_list|,
name|remoteRequest
argument_list|)
expr_stmt|;
block|}
name|remoteRequest
operator|.
name|setNumContainers
argument_list|(
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
name|containerCount
argument_list|)
expr_stmt|;
comment|// Note this down for next interaction with ResourceManager
name|addResourceRequestToAsk
argument_list|(
name|remoteRequest
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
literal|"addResourceRequest:"
operator|+
literal|" applicationId="
operator|+
name|appAttemptId
operator|+
literal|" priority="
operator|+
name|priority
operator|.
name|getPriority
argument_list|()
operator|+
literal|" resourceName="
operator|+
name|resourceName
operator|+
literal|" numContainers="
operator|+
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|" #asks="
operator|+
name|ask
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|decResourceRequest (Priority priority, String resourceName, Resource capability, int containerCount)
specifier|private
name|void
name|decResourceRequest
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|int
name|containerCount
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
name|remoteRequests
init|=
name|this
operator|.
name|remoteRequestsTable
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteRequests
operator|==
literal|null
condition|)
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
literal|"Not decrementing resource as priority "
operator|+
name|priority
operator|+
literal|" is not present in request table"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
name|reqMap
init|=
name|remoteRequests
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqMap
operator|==
literal|null
condition|)
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
literal|"Not decrementing resource as "
operator|+
name|resourceName
operator|+
literal|" is not present in request table"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|ResourceRequest
name|remoteRequest
init|=
name|reqMap
operator|.
name|get
argument_list|(
name|capability
argument_list|)
decl_stmt|;
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
literal|"BEFORE decResourceRequest:"
operator|+
literal|" applicationId="
operator|+
name|appAttemptId
operator|+
literal|" priority="
operator|+
name|priority
operator|.
name|getPriority
argument_list|()
operator|+
literal|" resourceName="
operator|+
name|resourceName
operator|+
literal|" numContainers="
operator|+
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|" #asks="
operator|+
name|ask
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|remoteRequest
operator|.
name|setNumContainers
argument_list|(
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|-
name|containerCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|<
literal|0
condition|)
block|{
comment|// guard against spurious removals
name|remoteRequest
operator|.
name|setNumContainers
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// send the ResourceRequest to RM even if is 0 because it needs to override
comment|// a previously sent value. If ResourceRequest was not sent previously then
comment|// sending 0 aught to be a no-op on RM
name|addResourceRequestToAsk
argument_list|(
name|remoteRequest
argument_list|)
expr_stmt|;
comment|// delete entries from map if no longer needed
if|if
condition|(
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|==
literal|0
condition|)
block|{
name|reqMap
operator|.
name|remove
argument_list|(
name|capability
argument_list|)
expr_stmt|;
if|if
condition|(
name|reqMap
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|remoteRequests
operator|.
name|remove
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|remoteRequests
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|remoteRequestsTable
operator|.
name|remove
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
block|}
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
name|info
argument_list|(
literal|"AFTER decResourceRequest:"
operator|+
literal|" applicationId="
operator|+
name|appAttemptId
operator|+
literal|" priority="
operator|+
name|priority
operator|.
name|getPriority
argument_list|()
operator|+
literal|" resourceName="
operator|+
name|resourceName
operator|+
literal|" numContainers="
operator|+
name|remoteRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|" #asks="
operator|+
name|ask
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

