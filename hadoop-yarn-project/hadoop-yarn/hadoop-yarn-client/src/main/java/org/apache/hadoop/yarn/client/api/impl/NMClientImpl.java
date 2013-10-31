begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
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
name|nio
operator|.
name|ByteBuffer
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|AtomicBoolean
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
name|security
operator|.
name|token
operator|.
name|SecretManager
operator|.
name|InvalidToken
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
name|ContainerState
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
name|NodeId
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
name|NMClient
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
name|ContainerManagementProtocolProxy
operator|.
name|ContainerManagementProtocolProxyData
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
name|ipc
operator|.
name|RPCUtil
import|;
end_import

begin_comment
comment|/**  *<p>  * This class implements {@link NMClient}. All the APIs are blocking.  *</p>  *  *<p>  * By default, this client stops all the running containers that are started by  * it when it stops. It can be disabled via  * {@link #cleanupRunningContainersOnStop}, in which case containers will  * continue to run even after this client is stopped and till the application  * runs at which point ResourceManager will forcefully kill them.  *</p>  *  *<p>  * Note that the blocking APIs ensure the RPC calls to<code>NodeManager</code>  * are executed immediately, and the responses are received before these APIs  * return. However, when {@link #startContainer} or {@link #stopContainer}  * returns,<code>NodeManager</code> may still need some time to either start  * or stop the container because of its asynchronous implementation. Therefore,  * {@link #getContainerStatus} is likely to return a transit container status  * if it is executed immediately after {@link #startContainer} or  * {@link #stopContainer}.  *</p>  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|NMClientImpl
specifier|public
class|class
name|NMClientImpl
extends|extends
name|NMClient
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
name|NMClientImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// The logically coherent operations on startedContainers is synchronized to
comment|// ensure they are atomic
DECL|field|startedContainers
specifier|protected
name|ConcurrentMap
argument_list|<
name|ContainerId
argument_list|,
name|StartedContainer
argument_list|>
name|startedContainers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ContainerId
argument_list|,
name|StartedContainer
argument_list|>
argument_list|()
decl_stmt|;
comment|//enabled by default
DECL|field|cleanupRunningContainers
specifier|private
specifier|final
name|AtomicBoolean
name|cleanupRunningContainers
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|cmProxy
specifier|private
name|ContainerManagementProtocolProxy
name|cmProxy
decl_stmt|;
DECL|method|NMClientImpl ()
specifier|public
name|NMClientImpl
parameter_list|()
block|{
name|super
argument_list|(
name|NMClientImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|NMClientImpl (String name)
specifier|public
name|NMClientImpl
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
comment|// Usually, started-containers are stopped when this client stops. Unless
comment|// the flag cleanupRunningContainers is set to false.
if|if
condition|(
name|getCleanupRunningContainers
argument_list|()
operator|.
name|get
argument_list|()
condition|)
block|{
name|cleanupRunningContainers
argument_list|()
expr_stmt|;
block|}
name|cmProxy
operator|.
name|stopAllProxies
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|cleanupRunningContainers ()
specifier|protected
specifier|synchronized
name|void
name|cleanupRunningContainers
parameter_list|()
block|{
for|for
control|(
name|StartedContainer
name|startedContainer
range|:
name|startedContainers
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|stopContainer
argument_list|(
name|startedContainer
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|startedContainer
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to stop Container "
operator|+
name|startedContainer
operator|.
name|getContainerId
argument_list|()
operator|+
literal|"when stopping NMClientImpl"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to stop Container "
operator|+
name|startedContainer
operator|.
name|getContainerId
argument_list|()
operator|+
literal|"when stopping NMClientImpl"
argument_list|)
expr_stmt|;
block|}
block|}
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
if|if
condition|(
name|getNMTokenCache
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"NMTokenCache has not been set"
argument_list|)
throw|;
block|}
name|cmProxy
operator|=
operator|new
name|ContainerManagementProtocolProxy
argument_list|(
name|conf
argument_list|,
name|getNMTokenCache
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cleanupRunningContainersOnStop (boolean enabled)
specifier|public
name|void
name|cleanupRunningContainersOnStop
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|getCleanupRunningContainers
argument_list|()
operator|.
name|set
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
block|}
DECL|class|StartedContainer
specifier|protected
specifier|static
class|class
name|StartedContainer
block|{
DECL|field|containerId
specifier|private
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|state
specifier|private
name|ContainerState
name|state
decl_stmt|;
DECL|method|StartedContainer (ContainerId containerId, NodeId nodeId, Token containerToken)
specifier|public
name|StartedContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|Token
name|containerToken
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|state
operator|=
name|ContainerState
operator|.
name|NEW
expr_stmt|;
block|}
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|containerId
return|;
block|}
DECL|method|getNodeId ()
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
block|}
DECL|method|addStartingContainer (StartedContainer startedContainer)
specifier|private
name|void
name|addStartingContainer
parameter_list|(
name|StartedContainer
name|startedContainer
parameter_list|)
throws|throws
name|YarnException
block|{
if|if
condition|(
name|startedContainers
operator|.
name|putIfAbsent
argument_list|(
name|startedContainer
operator|.
name|containerId
argument_list|,
name|startedContainer
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
literal|"Container "
operator|+
name|startedContainer
operator|.
name|containerId
operator|.
name|toString
argument_list|()
operator|+
literal|" is already started"
argument_list|)
throw|;
block|}
name|startedContainers
operator|.
name|put
argument_list|(
name|startedContainer
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|startedContainer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startContainer ( Container container, ContainerLaunchContext containerLaunchContext)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|startContainer
parameter_list|(
name|Container
name|container
parameter_list|,
name|ContainerLaunchContext
name|containerLaunchContext
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// Do synchronization on StartedContainer to prevent race condition
comment|// between startContainer and stopContainer only when startContainer is
comment|// in progress for a given container.
name|StartedContainer
name|startingContainer
init|=
name|createStartedContainer
argument_list|(
name|container
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|startingContainer
init|)
block|{
name|addStartingContainer
argument_list|(
name|startingContainer
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|allServiceResponse
decl_stmt|;
name|ContainerManagementProtocolProxyData
name|proxy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|proxy
operator|=
name|cmProxy
operator|.
name|getProxy
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|StartContainerRequest
name|scRequest
init|=
name|StartContainerRequest
operator|.
name|newInstance
argument_list|(
name|containerLaunchContext
argument_list|,
name|container
operator|.
name|getContainerToken
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StartContainerRequest
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|StartContainerRequest
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|scRequest
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|allRequests
init|=
name|StartContainersRequest
operator|.
name|newInstance
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|StartContainersResponse
name|response
init|=
name|proxy
operator|.
name|getContainerManagementProtocol
argument_list|()
operator|.
name|startContainers
argument_list|(
name|allRequests
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|containsKey
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|Throwable
name|t
init|=
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|get
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|deSerialize
argument_list|()
decl_stmt|;
name|parseAndThrowException
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|allServiceResponse
operator|=
name|response
operator|.
name|getAllServicesMetaData
argument_list|()
expr_stmt|;
name|startingContainer
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|RUNNING
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|startingContainer
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|COMPLETE
expr_stmt|;
comment|// Remove the started container if it failed to start
name|removeStartedContainer
argument_list|(
name|startingContainer
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|startingContainer
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|COMPLETE
expr_stmt|;
name|removeStartedContainer
argument_list|(
name|startingContainer
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|startingContainer
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|COMPLETE
expr_stmt|;
name|removeStartedContainer
argument_list|(
name|startingContainer
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|t
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|cmProxy
operator|.
name|mayBeCloseProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|allServiceResponse
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|stopContainer (ContainerId containerId, NodeId nodeId)
specifier|public
name|void
name|stopContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|StartedContainer
name|startedContainer
init|=
name|getStartedContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
comment|// Only allow one request of stopping the container to move forward
comment|// When entering the block, check whether the precursor has already stopped
comment|// the container
if|if
condition|(
name|startedContainer
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|startedContainer
init|)
block|{
if|if
condition|(
name|startedContainer
operator|.
name|state
operator|!=
name|ContainerState
operator|.
name|RUNNING
condition|)
block|{
return|return;
block|}
name|stopContainerInternal
argument_list|(
name|containerId
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
comment|// Only after successful
name|startedContainer
operator|.
name|state
operator|=
name|ContainerState
operator|.
name|COMPLETE
expr_stmt|;
name|removeStartedContainer
argument_list|(
name|startedContainer
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|stopContainerInternal
argument_list|(
name|containerId
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getContainerStatus (ContainerId containerId, NodeId nodeId)
specifier|public
name|ContainerStatus
name|getContainerStatus
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|ContainerManagementProtocolProxyData
name|proxy
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|containerIds
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
try|try
block|{
name|proxy
operator|=
name|cmProxy
operator|.
name|getProxy
argument_list|(
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
name|GetContainerStatusesResponse
name|response
init|=
name|proxy
operator|.
name|getContainerManagementProtocol
argument_list|()
operator|.
name|getContainerStatuses
argument_list|(
name|GetContainerStatusesRequest
operator|.
name|newInstance
argument_list|(
name|containerIds
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|containsKey
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|Throwable
name|t
init|=
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
operator|.
name|deSerialize
argument_list|()
decl_stmt|;
name|parseAndThrowException
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|ContainerStatus
name|containerStatus
init|=
name|response
operator|.
name|getContainerStatuses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|containerStatus
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|cmProxy
operator|.
name|mayBeCloseProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|stopContainerInternal (ContainerId containerId, NodeId nodeId)
specifier|private
name|void
name|stopContainerInternal
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|ContainerManagementProtocolProxyData
name|proxy
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|containerIds
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
try|try
block|{
name|proxy
operator|=
name|cmProxy
operator|.
name|getProxy
argument_list|(
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
name|StopContainersResponse
name|response
init|=
name|proxy
operator|.
name|getContainerManagementProtocol
argument_list|()
operator|.
name|stopContainers
argument_list|(
name|StopContainersRequest
operator|.
name|newInstance
argument_list|(
name|containerIds
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|containsKey
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|Throwable
name|t
init|=
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
operator|.
name|deSerialize
argument_list|()
decl_stmt|;
name|parseAndThrowException
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|cmProxy
operator|.
name|mayBeCloseProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createStartedContainer ( Container container)
specifier|protected
specifier|synchronized
name|StartedContainer
name|createStartedContainer
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|StartedContainer
name|startedContainer
init|=
operator|new
name|StartedContainer
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|container
operator|.
name|getContainerToken
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|startedContainer
return|;
block|}
specifier|protected
specifier|synchronized
name|void
DECL|method|removeStartedContainer (StartedContainer container)
name|removeStartedContainer
parameter_list|(
name|StartedContainer
name|container
parameter_list|)
block|{
name|startedContainers
operator|.
name|remove
argument_list|(
name|container
operator|.
name|containerId
argument_list|)
expr_stmt|;
block|}
DECL|method|getStartedContainer ( ContainerId containerId)
specifier|protected
specifier|synchronized
name|StartedContainer
name|getStartedContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
return|return
name|startedContainers
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
return|;
block|}
DECL|method|getCleanupRunningContainers ()
specifier|public
name|AtomicBoolean
name|getCleanupRunningContainers
parameter_list|()
block|{
return|return
name|cleanupRunningContainers
return|;
block|}
DECL|method|parseAndThrowException (Throwable t)
specifier|private
name|void
name|parseAndThrowException
parameter_list|(
name|Throwable
name|t
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|t
operator|instanceof
name|YarnException
condition|)
block|{
throw|throw
operator|(
name|YarnException
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|InvalidToken
condition|)
block|{
throw|throw
operator|(
name|InvalidToken
operator|)
name|t
throw|;
block|}
else|else
block|{
throw|throw
operator|(
name|IOException
operator|)
name|t
throw|;
block|}
block|}
block|}
end_class

end_unit

